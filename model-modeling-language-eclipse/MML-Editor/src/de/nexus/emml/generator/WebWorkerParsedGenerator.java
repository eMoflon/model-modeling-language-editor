package de.nexus.emml.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.nexus.emml.generator.entities.instance.GeneratorInstanceWrapper;
import de.nexus.emml.generator.entities.model.ModelEntity;

public class WebWorkerParsedGenerator {
    private ModelEntity typegraph;
    private GeneratorInstanceWrapper instancegraph;

    public ModelEntity getTypegraph() {
        return typegraph;
    }

    public GeneratorInstanceWrapper getInstancegraph() {
        return instancegraph;
    }

    public static WebWorkerParsedGenerator deserialize(String json) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(json, WebWorkerParsedGenerator.class);
    }
}
