package de.nexus.emml.generator.entities.instance;

import java.util.ArrayList;

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
