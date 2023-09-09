package de.nexus.emml.generator;

import java.net.URI;

import org.eclipse.core.runtime.Platform;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import de.nexus.emml.EditorActivator;
import de.nexus.emml.generator.entities.instance.GeneratorInstanceWrapper;
import de.nexus.emml.generator.entities.model.ModelEntity;

public class WebWorkerGeneratorEntry {
	private URI uri;
	private String gen;
	private WebWorkerParsedGenerator generatorResult;

	public static WebWorkerGeneratorEntry parse(String json) {
		return new Gson().fromJson(json, WebWorkerGeneratorEntry.class);
	}

	public static WebWorkerGeneratorEntry parse(JsonElement json) {
		return new Gson().fromJson(json, WebWorkerGeneratorEntry.class);
	}

	public ModelEntity getModel() {
		if (this.generatorResult == null) {
			this.generatorResult = WebWorkerParsedGenerator.deserialize(gen);
		}
		return this.generatorResult.getTypegraph();
	} 
	
	public GeneratorInstanceWrapper getInstances() {
		//Platform.getLog(EditorActivator.getDefault().getBundle()).info("[XMIBuilder DEBUG] Deserialize GEN: "+gen);
		if (this.generatorResult == null) {
			this.generatorResult = WebWorkerParsedGenerator.deserialize(gen);
		}
		return this.generatorResult.getInstancegraph();
	} 

	public URI getUri() {
		return uri;
	}
}
