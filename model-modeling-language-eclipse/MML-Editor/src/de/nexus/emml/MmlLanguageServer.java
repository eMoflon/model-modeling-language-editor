package de.nexus.emml;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wildwebdeveloper.embedder.node.NodeJSManager;

/**
 * MmlLanguageServer extending the abstract LanguageServerProcess
 * Defines language server startup command
 */
public class MmlLanguageServer extends LanguageServerProcess {
	public MmlLanguageServer(int port) {
		Platform.getLog(getClass()).info("Init MMLLanguageServer");
		List<String> commands = new ArrayList<>();
		commands.add(NodeJSManager.getNpmLocation().getAbsolutePath());
		Platform.getLog(getClass()).info("Added node path");
		try {
			URL url = FileLocator.toFileURL(getClass().getResource("/ls/index.html"));
			commands.add("exec");
			commands.add("-y");
			commands.add("http-server");
			commands.add("--");
			commands.add(new java.io.File(url.getPath()).getParentFile().getAbsolutePath());
			commands.add("-p");
			commands.add(String.valueOf(port));
			Platform.getLog(getClass()).info("Command: " + String.join(" ", commands));
			setCommands(commands);
			setWorkingDirectory(System.getProperty("user.dir"));
			start();
			Platform.getLog(getClass()).info("Init MMLLanguageServer done (?)");
		} catch (IOException e) {
			EditorActivator.getDefault().getLog().log(new Status(IStatus.ERROR,
					EditorActivator.getDefault().getBundle().getSymbolicName(), e.getMessage(), e));
		}
	}
}
