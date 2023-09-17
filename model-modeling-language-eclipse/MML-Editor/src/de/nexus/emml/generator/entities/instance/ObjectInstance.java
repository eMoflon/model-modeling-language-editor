package de.nexus.emml.generator.entities.instance;

import java.util.ArrayList;

/**
 * Dataclass for an instance
 */
public class ObjectInstance {
	private String referenceId;
	private String referenceTypeId;
	private String name;
	private ArrayList<AttributeEntry<?>> attributes;
	private ArrayList<ReferenceEntry> references;
	
	public String getReferenceId() {
		return referenceId;
	}
	
	public String getReferenceTypeId() {
		return referenceTypeId;
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<AttributeEntry<?>> getAttributes() {
		return attributes;
	}
	
	public ArrayList<ReferenceEntry> getReferences() {
		return references;
	}
}
