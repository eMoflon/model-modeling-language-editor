package de.nexus.emml;

public class MmlEditorSyncItem {
	private final String path;
	private final String text;
	
	public MmlEditorSyncItem(String path, String text) {
		this.path = path;
		this.text = text;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getText() {
		return text;
	}
}
