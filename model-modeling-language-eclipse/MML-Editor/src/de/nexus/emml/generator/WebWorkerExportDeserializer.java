package de.nexus.emml.generator;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class WebWorkerExportDeserializer implements JsonDeserializer<WebWorkerExportResult> {

	@Override
	public WebWorkerExportResult deserialize(JsonElement elm, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		JsonArray genArray = elm.getAsJsonObject().get("generator").getAsJsonArray();
		JsonArray diaArray = elm.getAsJsonObject().get("diagnostic").getAsJsonArray();

		ArrayList<WebWorkerGeneratorEntry> genEntries = genArray.asList().stream().map(entry -> WebWorkerGeneratorEntry.parse(entry))
				.collect(Collectors.toCollection(ArrayList::new));
		ArrayList<WebWorkerDiagnosticEntry> diaEntries = diaArray.asList().stream().map(entry -> WebWorkerDiagnosticEntry.parse(entry))
				.collect(Collectors.toCollection(ArrayList::new));
		return new WebWorkerExportResult(genEntries, diaEntries);
	}

}
