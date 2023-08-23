package de.nexus.emml.generator;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class NestedInitializerDeserializer implements JsonDeserializer<DeserializedGenerator> {

	@Override
	public DeserializedGenerator deserialize(JsonElement elm, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		JsonArray genArray = elm.getAsJsonObject().get("generator").getAsJsonArray();
		JsonArray diaArray = elm.getAsJsonObject().get("diagnostic").getAsJsonArray();

		ArrayList<GeneratorEntry> genEntries = genArray.asList().stream().map(entry -> GeneratorEntry.parse(entry))
				.collect(Collectors.toCollection(ArrayList::new));
		ArrayList<DiagnosticEntry> diaEntries = diaArray.asList().stream().map(entry -> DiagnosticEntry.parse(entry))
				.collect(Collectors.toCollection(ArrayList::new));
		return new DeserializedGenerator(genEntries, diaEntries);
	}

}
