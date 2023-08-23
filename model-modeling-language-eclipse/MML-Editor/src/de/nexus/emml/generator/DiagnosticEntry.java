package de.nexus.emml.generator;

import java.net.URI;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class DiagnosticEntry {
	private URI uri;
	private int err;

	public static DiagnosticEntry parse(String json) {
		return new Gson().fromJson(json, DiagnosticEntry.class);
	}

	public static DiagnosticEntry parse(JsonElement json) {
		return new Gson().fromJson(json, DiagnosticEntry.class);
	}

	public URI getUri() {
		return uri;
	}

	public int getErrorCount() {
		return err;
	}
}
