package de.nexus.emml.generator;

import java.net.URI;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class WebWorkerDiagnosticEntry {
	private URI uri;
	private int err;

	public static WebWorkerDiagnosticEntry parse(String json) {
		return new Gson().fromJson(json, WebWorkerDiagnosticEntry.class);
	}

	public static WebWorkerDiagnosticEntry parse(JsonElement json) {
		return new Gson().fromJson(json, WebWorkerDiagnosticEntry.class);
	}

	public URI getUri() {
		return uri;
	}

	public int getErrorCount() {
		return err;
	}
}
