package de.nexus.emml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Platform;

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
						.info("[PATH MODIFIER] child: "+child.toString());
						Platform.getLog(EditorActivator.getDefault().getBundle())
						.info("[PATH MODIFIER] relativize: "+base.toPath().relativize(child).toString());
						initializer.models.add(new MmlEditorSyncItem(base.toPath().relativize(child).toString(), childContent));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return initializer;
	}
	
	private static String readFile(Path filePath) throws IOException {
		return Files.readString(filePath);
	}
}
