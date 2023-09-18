package de.nexus.emml;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Platform;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MmlEditorSyncInitializer {
	private final ArrayList<MmlEditorSyncItem> models = new ArrayList<>();

	public MmlEditorSyncInitializer() {

	}

	public ArrayList<MmlEditorSyncItem> getModels() {
		return models;
	}

	public static MmlEditorSyncInitializer build(File base) {
		Platform.getLog(EditorActivator.getDefault().getBundle())
				.info("[WORKSPACE LOADER] LOAD FILES (" + base.getAbsolutePath() + ")...");
		MmlEditorSyncInitializer initializer = new MmlEditorSyncInitializer();
		try {
			Platform.getLog(EditorActivator.getDefault().getBundle()).info("[WORKSPACE LOADER] UNFITERED FILE LIST: "
					+ Files.walk(base.toPath()).filter(Files::isRegularFile).count());
			Files.walk(base.toPath()).filter(Files::isRegularFile).filter(t -> t.toString().endsWith(".mml"))
					.forEach(t -> Platform.getLog(EditorActivator.getDefault().getBundle())
							.info("[WORKSPACE LOADER DEBUG]   -" + t.toString()));
			List<Path> directoryList = Files.walk(base.toPath()).filter(Files::isRegularFile)
					.filter(t -> t.toString().endsWith(".mml")).collect(Collectors.toList());
			Platform.getLog(EditorActivator.getDefault().getBundle())
					.info("[WORKSPACE LOADER] FILTERED FILE LIST: " + directoryList.size() + "");
			if (directoryList != null) {
				for (Path child : directoryList) {
					try {
						String childContent = readFile(child);
						Platform.getLog(EditorActivator.getDefault().getBundle())
								.info("[PATH MODIFIER] child: " + child.toString());
						Platform.getLog(EditorActivator.getDefault().getBundle())
								.info("[PATH MODIFIER] relativize: " + base.toPath().relativize(child).toString());
						initializer.models
								.add(new MmlEditorSyncItem(base.toPath().relativize(child).toString(), childContent));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return initializer;
	}
	
	public static MmlEditorSyncInitializer buildExcludingCurrent(File base, String modelId) {
		Platform.getLog(EditorActivator.getDefault().getBundle()).info(
				"[WORKSPACE LOADER] LOAD INITIALIZER EXCLUDING MODEL WITH ID: "+modelId);
		MmlEditorSyncInitializer initializer = MmlEditorSyncInitializer.build(base);
		initializer.models.removeIf(e -> e.getPath().replace("\\", "/").equals(modelId.replace("\\", "/")));
		return initializer;
	}
	
	public static MmlEditorSyncInitializer buildCurrent(File base, String modelId) {
		Platform.getLog(EditorActivator.getDefault().getBundle()).info(
				"[WORKSPACE LOADER] LOAD INITIALIZER FOR MODEL WITH ID: "+modelId);
		MmlEditorSyncInitializer initializer = MmlEditorSyncInitializer.build(base);
		initializer.models.removeIf(e -> !e.getPath().replace("\\", "/").equals(modelId.replace("\\", "/")));
		return initializer;
	}

	public static MmlEditorSyncResult parseAndOverwriteAllFiles(File base, String initializerString) {
		Platform.getLog(EditorActivator.getDefault().getBundle()).info(
				"[WORKSPACE LOADER] LOAD SYNC ITEMS FROM INITIALIZER FOR BASE (" + base.getAbsolutePath() + ")...");
		Type listType = new TypeToken<ArrayList<MmlEditorSyncItem>>() {
		}.getType();
		ArrayList<MmlEditorSyncItem> modelList = new Gson().fromJson(initializerString, listType);
		int success = 0;
		int processed = 0;
		for (MmlEditorSyncItem item : modelList) {
			if (item.getPath().startsWith("file:///")) {
				String filePath = item.getPath().replaceFirst("file:///", "");
				Path absoluteFilePath = Path.of(base.getAbsolutePath().toString(), filePath);
				Platform.getLog(EditorActivator.getDefault().getBundle())
						.info("[WORKSPACE SYNC] (1) " + base.toString());
				Platform.getLog(EditorActivator.getDefault().getBundle()).info("[WORKSPACE SYNC] (2) " + filePath);
				Platform.getLog(EditorActivator.getDefault().getBundle())
						.info("[WORKSPACE SYNC] (3) " + absoluteFilePath.toString());
				processed++;
				try {
					writeFile(absoluteFilePath, item.getText());
					success++;
				} catch (IOException e) {
					Platform.getLog(EditorActivator.getDefault().getBundle())
							.info("[WORKSPACE SYNC] Could not write file: " + absoluteFilePath.toString());
					e.printStackTrace();
				}
			}
		}
		Platform.getLog(EditorActivator.getDefault().getBundle())
				.info(String.format("[WORKSPACE SYNC] Successfully saved %d of %d models!", success, processed));
		return new MmlEditorSyncResult(processed, success);
	}
	
	public static MmlEditorSyncResult parseAndOverwrite(File base, String initializerString,String modelId) {
		Platform.getLog(EditorActivator.getDefault().getBundle()).info(
				"[WORKSPACE LOADER] LOAD SYNC ITEMS FROM INITIALIZER FOR BASE (" + base.getAbsolutePath() + ")...");
		Type listType = new TypeToken<ArrayList<MmlEditorSyncItem>>() {
		}.getType();
		ArrayList<MmlEditorSyncItem> modelList = new Gson().fromJson(initializerString, listType);
		int success = 0;
		int processed = 0;
		for (MmlEditorSyncItem item : modelList) {
			if (item.getPath().startsWith("file:///")) {
				String filePath = item.getPath().replaceFirst("file:///", "");
				if (!filePath.equals(modelId)) {
					Platform.getLog(EditorActivator.getDefault().getBundle())
					.info("[WORKSPACE SYNC] Id does not match: " + filePath +" != "+modelId);
					continue;
				}
				Path absoluteFilePath = Path.of(base.getAbsolutePath().toString(), filePath);
				Platform.getLog(EditorActivator.getDefault().getBundle())
						.info("[WORKSPACE SYNC] (1) " + base.toString());
				Platform.getLog(EditorActivator.getDefault().getBundle()).info("[WORKSPACE SYNC] (2) " + filePath);
				Platform.getLog(EditorActivator.getDefault().getBundle())
						.info("[WORKSPACE SYNC] (3) " + absoluteFilePath.toString());
				processed++;
				try {
					writeFile(absoluteFilePath, item.getText());
					success++;
				} catch (IOException e) {
					Platform.getLog(EditorActivator.getDefault().getBundle())
							.info("[WORKSPACE SYNC] Could not write file: " + absoluteFilePath.toString());
					e.printStackTrace();
				}
				break;
			}
		}
		Platform.getLog(EditorActivator.getDefault().getBundle())
				.info(String.format("[WORKSPACE SYNC] Successfully saved %d of %d models!", success, processed));
		return new MmlEditorSyncResult(processed, success);
	}

	private static String readFile(Path filePath) throws IOException {
		return Files.readString(filePath);
	}
	
	private static void writeFile(Path filePath, String text) throws IOException {
		Files.writeString(filePath, text);
	}
}
