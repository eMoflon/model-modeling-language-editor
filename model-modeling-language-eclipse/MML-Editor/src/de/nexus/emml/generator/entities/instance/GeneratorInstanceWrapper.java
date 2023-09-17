package de.nexus.emml.generator.entities.instance;

import java.util.ArrayList;

/**
 * Dataclass for list of generators for multiple XMI files
 */
public class GeneratorInstanceWrapper {
	private ArrayList<GeneratorInstance> serializedInstances;
	
	public ArrayList<GeneratorInstance> getSerializedInstances() {
		return serializedInstances;
	}
}
