package de.nexus.emml.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MmlEntityTemplates {
	public static MmlGeneratorResult deserialize(String json) {
		GsonBuilder builder = new GsonBuilder();
		// builder.registerTypeAdapter(DeserializedGenerator.class, new
		// NestedInitializerDeserializer());
		Gson gson = builder.create();
		return gson.fromJson(json, MmlGeneratorResult.class);
	}
}