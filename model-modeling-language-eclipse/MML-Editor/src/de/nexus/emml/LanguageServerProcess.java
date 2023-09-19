package de.nexus.emml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

import org.eclipse.core.runtime.Platform;
import org.eclipse.wildwebdeveloper.embedder.node.NodeJSManager;

/**
 * Abstract langage server process
 * Provides functionality for language server processes in the background 
 * as well as IO-Operations 
 */
public abstract class LanguageServerProcess {
	private Process process;
	private List<String> commands;
	private String workingDir;

	public LanguageServerProcess() {
	}

	public LanguageServerProcess(List<String> commands, String workingDir) {
		this.commands = commands;
		this.workingDir = workingDir;
	}

	public void start() throws IOException {
		if (this.workingDir == null || this.commands == null || this.commands.isEmpty()
				|| this.commands.stream().anyMatch(Objects::isNull)) {
			throw new IOException("Unable to start language server: " + this.toString()); //$NON-NLS-1$
		}

		ProcessBuilder builder = createProcessBuilder();
		Process p = builder.start();
		this.process = p;
		if (!p.isAlive()) {
			throw new IOException("Unable to start language server: " + this.toString()); //$NON-NLS-1$
		}
		Platform.getLog(getClass()).info("Created process " + String.valueOf(p.pid()));
	}

	protected ProcessBuilder createProcessBuilder() {
		ProcessBuilder builder = new ProcessBuilder(getCommands());
		
		// We need to extend the PATH variable to be compatible with systems without local node installation.
		builder.environment().put("PATH", System.getenv("PATH")+File.pathSeparator+NodeJSManager.getNodeJsLocation().getParentFile().getAbsolutePath());
		
		builder.directory(new File(getWorkingDirectory()));
		builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		builder.redirectError(ProcessBuilder.Redirect.INHERIT);
		return builder;
	}

	public InputStream getInputStream() {
		Process p = process;
		return p == null ? null : p.getInputStream();
	}

	public InputStream getErrorStream() {
		Process p = process;
		return p == null ? null : p.getErrorStream();
	}

	public OutputStream getOutputStream() {
		Process p = process;
		return p == null ? null : p.getOutputStream();
	}

	public void stop() {
		Process p = process;
		if (p != null) {
			Platform.getLog(getClass()).info("Trying to destroy process");
			Platform.getLog(getClass()).info("PID: " + String.valueOf(p.pid()));
			Platform.getLog(getClass()).info("Childcount: " + String.valueOf(p.children().count()));
			p.descendants().forEach(child -> child.destroyForcibly());
			p.destroyForcibly();
		} else {
			Platform.getLog(getClass()).warn("Could not destroy process!");
		}
	}

	protected List<String> getCommands() {
		return commands;
	}

	public void setCommands(List<String> commands) {
		this.commands = commands;
	}

	protected String getWorkingDirectory() {
		return workingDir;
	}

	public void setWorkingDirectory(String workingDir) {
		this.workingDir = workingDir;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof LanguageServerProcess)) {
			return false;
		}
		LanguageServerProcess other = (LanguageServerProcess) obj;
		return Objects.equals(this.getCommands(), other.getCommands())
				&& Objects.equals(this.getWorkingDirectory(), other.getWorkingDirectory());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getCommands(), this.getWorkingDirectory());
	}
}
