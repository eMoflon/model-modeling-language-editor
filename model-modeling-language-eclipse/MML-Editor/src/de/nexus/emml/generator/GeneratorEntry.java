package de.nexus.emml.generator;

import java.net.URI;

import org.eclipse.core.runtime.Platform;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import de.nexus.emml.EditorActivator;
import de.nexus.emml.generator.entities.instance.GeneratorInstanceWrapper;
import de.nexus.emml.generator.entities.model.ModelEntity;

public class GeneratorEntry {
	private URI uri;
	private String gen;
	private MmlGeneratorResult generatorResult;

	public static GeneratorEntry parse(String json) {
		return new Gson().fromJson(json, GeneratorEntry.class);
	}

	public static GeneratorEntry parse(JsonElement json) {
		return new Gson().fromJson(json, GeneratorEntry.class);
	}

	public ModelEntity getModel() {
		if (this.generatorResult == null) {
			this.generatorResult = MmlEntityTemplates.deserialize(gen);
		}
		return this.generatorResult.getTypegraph();
	} 
	
	public GeneratorInstanceWrapper getInstances() {
		Platform.getLog(EditorActivator.getDefault().getBundle()).info("[XMIBuilder DEBUG] Deserialize GEN: "+gen);
		if (this.generatorResult == null) {
			this.generatorResult = MmlEntityTemplates.deserialize(gen);
		}
		return this.generatorResult.getInstancegraph();
	} 

	public URI getUri() {
		return uri;
	}
}
