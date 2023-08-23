package de.nexus.emml.generator;

import java.net.URI;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.nexus.emml.generator.entities.ModelEntity;

public class GeneratorEntry {
	private URI uri;
	private String gen;

	public static GeneratorEntry parse(String json) {
		return new Gson().fromJson(json, GeneratorEntry.class);
	}

	public static GeneratorEntry parse(JsonElement json) {
		return new Gson().fromJson(json, GeneratorEntry.class);
	}

	public ModelEntity getModel() {
		return MmlEntityTemplates.deserialize(gen);
	}

	public URI getUri() {
		return uri;
	}
}
