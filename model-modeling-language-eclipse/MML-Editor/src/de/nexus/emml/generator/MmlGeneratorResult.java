package de.nexus.emml.generator;

import de.nexus.emml.generator.entities.instance.GeneratorInstanceWrapper;
import de.nexus.emml.generator.entities.model.ModelEntity;

public class MmlGeneratorResult {
	private ModelEntity typegraph;
	private GeneratorInstanceWrapper instancegraph;
	
	public ModelEntity getTypegraph() {
		return typegraph;
	}
	
	public GeneratorInstanceWrapper getInstancegraph() {
		return instancegraph;
	}
}
