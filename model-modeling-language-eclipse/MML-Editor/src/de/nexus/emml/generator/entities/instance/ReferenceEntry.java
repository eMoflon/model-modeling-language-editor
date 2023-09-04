package de.nexus.emml.generator.entities.instance;

import java.util.ArrayList;

public class ReferenceEntry {
	private String name;
	private String typeId;
	private ArrayList<String> referencedIds;
	
	public String getName() {
		return name;
	}
	
	public String getTypeId() {
		return typeId;
	}
	
	public ArrayList<String> getReferencedIds() {
		return referencedIds;
	}
}
