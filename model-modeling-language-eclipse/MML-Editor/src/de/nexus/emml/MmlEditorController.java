package de.nexus.emml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;

import com.google.gson.Gson;

import de.nexus.emml.generator.DeserializedGenerator;
import de.nexus.emml.generator.EcoreTypeGraphBuilder;
import de.nexus.emml.generator.GeneratorDeserializer;
import de.nexus.emml.generator.GeneratorEntry;
import de.nexus.emml.generator.entities.ModelEntity;
import de.nexus.emml.generator.entities.PackageEntity;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class MmlEditorController implements Initializable {

	@FXML
	private WebView webView = new WebView();

	@FXML
	private MenuItem updateMenuItem;

	@FXML
	private MenuItem saveMenuItem;

	@FXML
	private MenuItem exportMenuItem;

	@FXML
	private MenuItem quitMenuItem;

	private WebEngine engine;

	private File basePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();

	public MmlEditorController() {
		Platform.getLog(getClass()).info("Init MmlEditor");

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Platform.getLog(getClass()).info("Calling initializer for Controller");
		this.engine = webView.getEngine();
		webView.getEngine().getLoadWorker().stateProperty().addListener(x -> {
			if (webView.getEngine().getLoadWorker().getState().equals(State.FAILED)) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				Platform.getLog(getClass()).info("[WORKER STATE OBSERVER] Worker failed - reloading...");
				updateWebView();
			}
			Platform.getLog(getClass()).info("[LIFECYCLE] " + webView.getEngine().getLoadWorker().getState().name());
			Platform.getLog(getClass())
					.info("[LIFECYCLE Inspect] " + webView.getEngine().getLoadWorker().getTitle() + " | "
							+ webView.getEngine().getLoadWorker().getMessage() + " | "
							+ webView.getEngine().getLoadWorker().getProgress());
			if (webView.getEngine().getLoadWorker().getState().equals(State.RUNNING)
					&& webView.getEngine().getLoadWorker().getMessage().contains("Loading http://localhost")) {
				Platform.getLog(getClass())
						.info("[WORKER STATE OBSERVER] Worker succeeded - initialize editor | "
								+ webView.getEngine().getLoadWorker().getTitle() + " | "
								+ webView.getEngine().getLoadWorker().getMessage() + " | "
								+ webView.getEngine().getLoadWorker().getProgress());
				Gson gson = new Gson();
				MmlEditorSyncInitializer syncInitializer = MmlEditorSyncInitializer.build(basePath);
				Platform.getLog(getClass()).info("[EDITOR INITIALIZER] INITIALIZE EDITOR...");
				String serializedInitializer = gson.toJson(syncInitializer.getModels());
				String escapedSerializedInitializer = StringEscapeUtils.escapeJavaScript(serializedInitializer);
				Platform.getLog(getClass()).info("[EDITOR INITIALIZER] " + escapedSerializedInitializer);
				int initWorkspace = (int) webView.getEngine().executeScript(
						String.format("initializeWorkspaceJson(\"%s\",`%s`)", basePath, escapedSerializedInitializer));
				Platform.getLog(getClass()).info("[EDITOR INITIALIZER] " + initWorkspace);
				if (initWorkspace == syncInitializer.getModels().size()) {
					Platform.getLog(getClass()).info("[EDITOR INITIALIZER] INITIALIZED SUCCSSFULLY");
					String modelId = getLastClickedFileModelName();
					Platform.getLog(getClass()).info("[EDITOR INITIALIZER] openModelResult: " + openModel(modelId));
				} else {
					Platform.getLog(getClass())
							.info(String.format(
									"[EDITOR INITIALIZER] INITIALIZED UNSUCCESSFULL (%d of %d models loaded)",
									syncInitializer.getModels().size(), initWorkspace));
				}
			}
		});
		updateWebView();
	}

	private String getLastClickedFileModelName() {
		Path path = EditorActivator.getDefault().getLastClickedFile();
		if (path == null) {
			Alert alert = new Alert(AlertType.ERROR, "Could not determine file to open!", ButtonType.CLOSE);
			alert.showAndWait();
		}
		String modelId = path.toString().replace("\\", "/");
		if (modelId.startsWith("/")) {
			modelId = modelId.replaceFirst("/", "");
		}
		Platform.getLog(getClass()).info(String.format("[GETLASTCLICKEDFILE] %s", modelId));
		return modelId;
	}

	private boolean openModel(String modelName) {
		return (boolean) webView.getEngine().executeScript(String.format("openModel(\"%s\")", modelName));
	}

	@FXML
	private void updateWebView() {
		Platform.getLog(getClass()).info("Update Webview");
		engine.setJavaScriptEnabled(true);
		int port = EditorActivator.MML_LS_PORT;
		engine.load("http://localhost:" + String.valueOf(port));
		webView.setVisible(true);

	}

	@FXML
	private void saveFile() {
		Platform.getLog(getClass()).info("Save file");
	}

	@FXML
	private void exportModel() {
		Platform.getLog(getClass()).info("Export model");
		Platform.getLog(getClass()).info("Export model");
		String genResult = (String) webView.getEngine().executeScript("getCombinedGeneratorResult()");
		Platform.getLog(getClass()).info(genResult.toString());
		DeserializedGenerator desGen = GeneratorDeserializer.deserialize(genResult);
		if (desGen.hasErrors()) {
			Alert alert = new Alert(AlertType.ERROR,
					"There are " + String.valueOf(desGen.getDiagnosticStorage().stream().map(x -> x.getErrorCount())
							.mapToInt(Integer::intValue).sum()) + " errors! Cannot export models!",
					ButtonType.CLOSE);
			alert.showAndWait();
		} else {
			Platform.getLog(getClass())
					.info("Successfully constructed documents: " + String.valueOf(desGen.getGeneratorStorage().size()));
			if (desGen.getGeneratorStorage().size() > 0) {
				Platform.getLog(getClass()).info("Deserialize model");
				ModelEntity modEntity = desGen.getGeneratorStorage().get(0).getModel();
				Platform.getLog(getClass()).info("Deserialization completed!");
				Platform.getLog(getClass()).info(modEntity.toString());
				Platform.getLog(getClass()).info("==========[Building ecore]==========");
				List<EcoreTypeGraphBuilder> builders = new ArrayList();
				for (GeneratorEntry genEntry : desGen.getGeneratorStorage()) {
					String projectName = Path.of(genEntry.getUri()).subpath(0, 1).toString();
					Path modelsDir = Paths.get(basePath.toString(),projectName,"models");
					try {
						Files.createDirectories(modelsDir);
					} catch (IOException ex) {
						Platform.getLog(getClass()).info("[MODEL PATH BUILDER] Could not create models directory: "+modelsDir.toString());	
					}
					ModelEntity model = genEntry.getModel();
					for (PackageEntity pckgEntity : model.getPackages()) {
						String fileName = Path.of(genEntry.getUri()).getFileName().toString().replace(".mml", "")+"_" + pckgEntity.getName()
						+ ".ecore";
						Path filePath = Paths.get(modelsDir.toString(),fileName);
						String packageUri = String.format("platform:/resource/%s/models/%s", projectName,fileName);
						builders.add(new EcoreTypeGraphBuilder(pckgEntity, packageUri,filePath.toString()));
						Platform.getLog(getClass()).info(String.format("	- %s [EXPORTED to %s]", pckgEntity.getName(),filePath.toString()));
					}
				}
				EcoreTypeGraphBuilder.buildEcoreFile(builders);
				Platform.getLog(getClass()).info("Ecore created!");
			}
		}
	}

	@FXML
	private void quitEditor() {
		Platform.getLog(getClass()).info("Quit editor");
		Platform.getLog(getClass()).info("Worker state: " + webView.getEngine().getLoadWorker().getState().name());
		Platform.getLog(getClass())
				.info("documentProperty: " + webView.getEngine().documentProperty() == null ? "isNull" : "isNotNull");
	}
}
