package de.nexus.emml.generator.entities;

public class AttributeEntity<T> {
	private String name;
	private String type;
	private boolean isEnumType;
	private boolean hasDefaultValue;
	private T defaultValue;
	private ClassElementModifiers modifiers;

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public boolean isEnumType() {
		return isEnumType;
	}

	public boolean isHasDefaultValue() {
		return hasDefaultValue;
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	public ClassElementModifiers getModifiers() {
		return modifiers;
	}

	@Override
	public String toString() {
		return String.format("%s<%s>", name, type);
	}
}
