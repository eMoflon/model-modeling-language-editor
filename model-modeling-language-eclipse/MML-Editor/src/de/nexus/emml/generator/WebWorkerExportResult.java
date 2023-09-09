package de.nexus.emml.generator;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Platform;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class WebWorkerExportResult {
    private final ArrayList<WebWorkerGeneratorEntry> generatorStorage;
    private final ArrayList<WebWorkerDiagnosticEntry> diagnosticStorage;

    public WebWorkerExportResult(ArrayList<WebWorkerGeneratorEntry> gen, ArrayList<WebWorkerDiagnosticEntry> dia) {
        this.generatorStorage = gen.stream().filter(x -> x.getUri().getScheme().equalsIgnoreCase("file"))
                .collect(Collectors.toCollection(ArrayList::new));
        this.diagnosticStorage = dia.stream().filter(x -> x.getUri().getScheme().equalsIgnoreCase("file"))
                .collect(Collectors.toCollection(ArrayList::new));

        for (WebWorkerGeneratorEntry entry : generatorStorage) {
            Platform.getLog(getClass())
                    .info("[POST] Gen path: " + entry.getUri().getScheme() + " | " + entry.getUri().getPath());
        }
    }

    public boolean hasErrors() {
        return this.diagnosticStorage.size() != 0;
    }

    public ArrayList<WebWorkerGeneratorEntry> getGeneratorStorage() {
        return generatorStorage;
    }

    public ArrayList<WebWorkerDiagnosticEntry> getDiagnosticStorage() {
        return diagnosticStorage;
    }

    public static WebWorkerExportResult deserialize(String json) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(WebWorkerExportResult.class, new WebWorkerExportDeserializer());
        Gson gson = builder.create();
        return gson.fromJson(json, WebWorkerExportResult.class);
    }
}
