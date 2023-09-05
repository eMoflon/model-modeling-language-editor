package de.nexus.emml;

import org.eclipse.core.runtime.Platform;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class MmlEditorClipboardManager {
	public String getPaste() {
		Platform.getLog(EditorActivator.getDefault().getBundle())
		.info("[MML CLIPBOARD] PASTE");
		final Clipboard sysClipboard = Clipboard.getSystemClipboard();
		if (sysClipboard.hasString()) {
			return sysClipboard.getString();
		}else {
			return "";
		}
	}	
	
	public void setCopy(String val) {
		Platform.getLog(EditorActivator.getDefault().getBundle())
		.info("[MML CLIPBOARD] COPY: " + val);
		final Clipboard sysClipboard = Clipboard.getSystemClipboard();
		final ClipboardContent content = new ClipboardContent();
        content.putString(val);
        sysClipboard.setContent(content);
        Platform.getLog(EditorActivator.getDefault().getBundle())
		.info("[MML CLIPBOARD] NEW CONTENT: " + sysClipboard.getString());
	}
	
	public void log(String val) {
		Platform.getLog(EditorActivator.getDefault().getBundle())
		.info("[MML CLIPBOARD] LOG: " + val);
	}
}
