package de.nexus.emml;

import java.nio.file.Path;
import java.util.function.Consumer;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import javafx.application.Application;

/**
 * The activator class controls the plug-in life cycle
 */
public class EditorActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "MML-Editor"; //$NON-NLS-1$
	public static final int MML_LS_PORT = 12345;

	// The shared instance
	private static EditorActivator plugin;
	private MmlLanguageServer langServer;
	
	private Consumer<String> openFileCallback;
	private Path lastClickedFile;

	/**
	 * The constructor
	 */
	public EditorActivator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		Platform.getLog(getClass()).info("Activator start");
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (plugin.langServer != null) {
			Platform.getLog(getClass()).info("Stopping lang server");
			plugin.langServer.stop();
		}
		plugin = null;
		Platform.getLog(getClass()).info("Activator stop");
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static EditorActivator getDefault() {
		return plugin;
	}

	public void openFile(Path file) {
		if (plugin == null) {
			Platform.getLog(getClass()).info("Plugin not running!");
			return;
		}
		Platform.getLog(getClass()).info("[ACTIVATOR] Open file: "+file.toString());
		this.lastClickedFile = file;
		if (plugin.langServer == null) {
			Platform.getLog(getClass()).info("Starting langserver");
			plugin.langServer = new MmlLanguageServer(MML_LS_PORT);
		} else {
			Platform.getLog(getClass()).info("Langserver is already running");
		}
		new Thread(() -> Application.launch(MmlEditor.class)).start();
	}
	
	public Path getLastClickedFile() {
		Path path = this.lastClickedFile;
		this.lastClickedFile = null;
		return path;
	}
}
