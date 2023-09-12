package de.nexus.emml;

import java.awt.Toolkit;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;
import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;

import com.google.gson.Gson;
import de.nexus.emml.generator.EmfResourceBuilder;
import de.nexus.emml.generator.WebWorkerExportResult;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import netscape.javascript.JSException;

public class MmlEditorController implements Initializable {

	@FXML
	private WebView webView = new WebView();

	@FXML
	private MenuItem updateCurrentMenuItem;

	@FXML
	private MenuItem updateOthersMenuItem;

	@FXML
	private MenuItem updateAllMenuItem;

	@FXML
	private MenuItem saveCurrentMenuItem;

	@FXML
	private MenuItem saveAllMenuItem;

	@FXML
	private MenuItem exportMenuItem;

	@FXML
	private MenuItem quitMenuItem;

	@FXML
	private MenuItem editCutItem;

	@FXML
	private MenuItem editCopyItem;

	@FXML
	private MenuItem editPasteItem;

	@FXML
	private VBox loadingVBox;

	private WebEngine engine;

	private File basePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();

	private String lastClickedModelId;

	public MmlEditorController() {
		Platform.getLog(getClass()).info("Init MmlEditor");
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Platform.getLog(getClass()).info("Calling initializer for Controller");
		this.engine = webView.getEngine();
		webView.getEngine().getLoadWorker().stateProperty().addListener(x -> {
			if (webView.getEngine().getLoadWorker().getState().equals(State.FAILED)) {
				Timeline tl = new Timeline(new KeyFrame(Duration.seconds(1), ae -> {
					Platform.getLog(getClass()).info("[WORKER STATE OBSERVER] Worker failed - reloading...");
					updateAllWebView();
				}));
				tl.setCycleCount(1);
				tl.play();
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
				int initWorkspace = -1;
				try {
					initWorkspace = (int) webView.getEngine().executeScript(String
							.format("initializeWorkspaceJson(\"%s\",`%s`)", basePath, escapedSerializedInitializer));
				} catch (JSException ex) {
					Platform.getLog(getClass())
							.info("[EDITOR INITIALIZER] Could not access workspace initializer - is it up yet?");
				}
				Platform.getLog(getClass()).info("[EDITOR INITIALIZER] " + initWorkspace);
				if (initWorkspace == syncInitializer.getModels().size()) {
					Platform.getLog(getClass()).info("[EDITOR INITIALIZER] INITIALIZED SUCCSSFULLY");
					String modelId = getLastClickedFileModelName();
					Platform.getLog(getClass()).info("[EDITOR INITIALIZER] openModelResult: " + openModel(modelId));
					this.loadingVBox.setDisable(true);
					this.loadingVBox.setVisible(false);
				} else {
					Platform.getLog(getClass())
							.info(String.format(
									"[EDITOR INITIALIZER] INITIALIZED UNSUCCESSFULL (%d of %d models loaded)",
									syncInitializer.getModels().size(), initWorkspace));
				}
			}
		});
		this.loadingVBox.setDisable(false);
		this.loadingVBox.setVisible(true);
		updateAllWebView();

		Toolkit.getDefaultToolkit().getSystemClipboard().addFlavorListener(new FlavorListener() {
			@Override
			public void flavorsChanged(FlavorEvent e) {
				Platform.getLog(getClass())
						.info(String.format("[CLIPBOARD UPDATE] %s | %s", e.getSource(), e.toString()));
			}
		});

		this.webView.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			final KeyCodeCombination keyComb = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);

			public void handle(KeyEvent ke) {
				if (keyComb.match(ke)) {
					onCopyAction();
					ke.consume(); // <-- stops passing the event to next node
				}
			}
		});

		this.webView.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			final KeyCodeCombination keyComb = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN);

			public void handle(KeyEvent ke) {
				if (keyComb.match(ke)) {
					onPasteAction();
					ke.consume(); // <-- stops passing the event to next node
				}
			}
		});

		this.webView.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			final KeyCodeCombination keyComb = new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN);

			public void handle(KeyEvent ke) {
				if (keyComb.match(ke)) {
					onCutAction();
					ke.consume(); // <-- stops passing the event to next node
				}
			}
		});
	}

	private String getLastClickedFileModelName() {
		Path path = EditorActivator.getDefault().getLastClickedFile();
		if (path == null) {
			if (this.lastClickedModelId == null) {
				Alert alert = new Alert(AlertType.ERROR, "Could not determine file to open!", ButtonType.CLOSE);
				alert.showAndWait();
			} else {
				return this.lastClickedModelId;
			}
		}
		String modelId = path.toString().replace("\\", "/");
		if (modelId.startsWith("/")) {
			modelId = modelId.replaceFirst("/", "");
		}
		Platform.getLog(getClass()).info(String.format("[GETLASTCLICKEDFILE] %s", modelId));

		this.lastClickedModelId = modelId;
		return modelId;
	}

	private boolean openModel(String modelName) {
		return (boolean) webView.getEngine().executeScript(String.format("openModel(\"%s\")", modelName));
	}

	@FXML
	private void updateCurrentWebView() {
		Platform.getLog(getClass()).info("Update Webview | Reload current model");
		Gson gson = new Gson();
		MmlEditorSyncInitializer syncInitializer = MmlEditorSyncInitializer.buildCurrent(basePath, lastClickedModelId);
		Platform.getLog(getClass()).info("[EDITOR UPDATER] INITIALIZE FILE UPDATE...");
		for (MmlEditorSyncItem syncItem : syncInitializer.getModels()) {
			Platform.getLog(getClass()).info("[EDITOR UPDATER >> ] " + syncItem.getPath() + " | " + lastClickedModelId);
			String serializedInitializer = gson.toJson(syncItem);
			String escapedSerializedInitializer = StringEscapeUtils.escapeJavaScript(serializedInitializer);
			Platform.getLog(getClass()).info("[EDITOR UPDATER] " + escapedSerializedInitializer);
			boolean updateSuccesful = (boolean) webView.getEngine()
					.executeScript(String.format("updateModelJson(`%s`)", escapedSerializedInitializer));
			Platform.getLog(getClass())
					.info("[EDITOR UPDATER] " + syncItem.getPath() + " -> " + String.valueOf(updateSuccesful));
		}
		Platform.getLog(getClass()).info("[EDITOR UPDATER] COMPLETE");
	}

	@FXML
	private void updateOthersWebView() {
		Platform.getLog(getClass()).info("Update Webview | Reload other models");
		Gson gson = new Gson();
		MmlEditorSyncInitializer syncInitializer = MmlEditorSyncInitializer.buildExcludingCurrent(basePath,
				lastClickedModelId);
		Platform.getLog(getClass()).info("[EDITOR UPDATER] INITIALIZE OTHER FILES UPDATE...");
		for (MmlEditorSyncItem syncItem : syncInitializer.getModels()) {
			String serializedInitializer = gson.toJson(syncItem);
			String escapedSerializedInitializer = StringEscapeUtils.escapeJavaScript(serializedInitializer);
			Platform.getLog(getClass()).info("[EDITOR UPDATER] " + escapedSerializedInitializer);
			boolean updateSuccesful = (boolean) webView.getEngine()
					.executeScript(String.format("updateModelJson(`%s`)", escapedSerializedInitializer));
			Platform.getLog(getClass())
					.info("[EDITOR UPDATER] " + syncItem.getPath() + " -> " + String.valueOf(updateSuccesful));
		}
		Platform.getLog(getClass()).info("[EDITOR UPDATER] COMPLETE");
	}

	@FXML
	private void updateAllWebView() {
		Platform.getLog(getClass()).info("Update Webview | Full reload");
		engine.setJavaScriptEnabled(true);
		int port = EditorActivator.MML_LS_PORT;
		engine.load("http://localhost:" + String.valueOf(port));
		webView.setVisible(true);
	}

	@FXML
	private void saveCurrentFile() {
		Platform.getLog(getClass()).info("Save current file");
		saveFile(true);
	}

	@FXML
	private void saveAllFiles() {
		Platform.getLog(getClass()).info("Save all file");
		saveFile(false);
	}

	private void saveFile(boolean justCurrent) {
		String exportedWorkspace = (String) webView.getEngine().executeScript("exportWorkspace()");
		Platform.getLog(getClass()).info(exportedWorkspace.toString());
		MmlEditorSyncResult result;
		if (justCurrent) {
			result = MmlEditorSyncInitializer.parseAndOverwrite(basePath, exportedWorkspace, lastClickedModelId);
		} else {
			result = MmlEditorSyncInitializer.parseAndOverwriteAllFiles(basePath, exportedWorkspace);
		}
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("ModelModelingLanguage - Editor");
		alert.setHeaderText("Saved models!");
		alert.setContentText(
				String.format("Successfully saved %d of %d models!", result.success(), result.processed()));
		alert.showAndWait();
	}

	@FXML
	private void exportModel() {
		Platform.getLog(getClass()).info("Export model");
		String genResult = (String) webView.getEngine().executeScript("getCombinedGeneratorResult()");
		Platform.getLog(getClass()).info(genResult.toString());
		WebWorkerExportResult desGen = WebWorkerExportResult.deserialize(genResult);
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
				EmfResourceBuilder.buildEmfResources(desGen.getGeneratorStorage(), basePath);
			}
		}
	}

	@FXML
	private void quitEditor() {
		Platform.getLog(getClass()).info("Quit editor");
		Platform.getLog(getClass()).info("Worker state: " + webView.getEngine().getLoadWorker().getState().name());
		Platform.getLog(getClass())
				.info("documentProperty: " + webView.getEngine().documentProperty() == null ? "isNull" : "isNotNull");
		this.webView.getScene().getWindow().hide();
	}

	@FXML
	private void onCutAction() {
		Platform.getLog(EditorActivator.getDefault().getBundle()).info("[MML CLIPBOARD] CUT");
		onCopyAction();
		this.webView.getEngine().executeScript("setPaste('')");
	}

	@FXML
	private void onCopyAction() {
		String copyContent = (String) this.webView.getEngine().executeScript("getCopy()");
		Platform.getLog(EditorActivator.getDefault().getBundle()).info("[MML CLIPBOARD] COPY: " + copyContent);
		final Clipboard sysClipboard = Clipboard.getSystemClipboard();
		sysClipboard.clear();
		final ClipboardContent content = new ClipboardContent();
		content.putString(copyContent);
		sysClipboard.setContent(content);
		Platform.getLog(EditorActivator.getDefault().getBundle())
				.info("[MML CLIPBOARD] NEW CONTENT: " + sysClipboard.getString());
	}

	@FXML
	private void onPasteAction() {
		Platform.getLog(EditorActivator.getDefault().getBundle()).info("[MML CLIPBOARD] PASTE");
		final Clipboard sysClipboard = Clipboard.getSystemClipboard();
		String pasteContent = "";
		if (sysClipboard.hasString()) {
			pasteContent = sysClipboard.getString();
		}
		pasteContent = StringEscapeUtils.escapeJavaScript(pasteContent);
		this.webView.getEngine().executeScript(String.format("setPaste('%s')", pasteContent));
	}

	public WebView getWebView() {
		return webView;
	}
}
