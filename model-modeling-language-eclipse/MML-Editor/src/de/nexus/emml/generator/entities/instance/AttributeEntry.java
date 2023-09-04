package de.nexus.emml.generator.entities.instance;

public class AttributeEntry<T> {
	private String name;
	private String typeId;
	private T value;
	private boolean isEnumType;
	
	public String getTypeId() {
		return typeId;
	}
	
	public String getName() {
		return name;
	}
	
	public T getValue() {
		return value;
	}
	
	public boolean isEnumType() {
		return isEnumType;
	}
}
