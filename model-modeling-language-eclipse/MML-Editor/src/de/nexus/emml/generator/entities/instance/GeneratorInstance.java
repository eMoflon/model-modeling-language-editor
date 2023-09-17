package de.nexus.emml.generator.entities.instance;

import java.util.ArrayList;

/**
 * Dataclass for a generator of a single XMI file
 */
public class GeneratorInstance {
	private String instanceName;
	private ArrayList<ObjectInstance> instances;
	
	public String getInstanceName() {
		return instanceName;
	}
	
	public ArrayList<ObjectInstance> getInstances() {
		return instances;
	}
}
