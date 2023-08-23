package de.nexus.emml.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.nexus.emml.generator.entities.ModelEntity;

public class MmlEntityTemplates {
	public static ModelEntity deserialize(String json) {
		GsonBuilder builder = new GsonBuilder();
		// builder.registerTypeAdapter(DeserializedGenerator.class, new
		// NestedInitializerDeserializer());
		Gson gson = builder.create();
		return gson.fromJson(json, ModelEntity.class);
	}
}