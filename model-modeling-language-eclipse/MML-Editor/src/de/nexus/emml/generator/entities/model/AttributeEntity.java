package de.nexus.emml.generator.entities.model;

/**
 * Dataclass for a class attribute
 */
public class AttributeEntity<T> {
	private String referenceId;
	private String name;
	private String type;
	private boolean isEnumType;
	private boolean hasDefaultValue;
	private T defaultValue;
	private ClassElementModifiers modifiers;

	public String getReferenceId() {
		return referenceId;
	}

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
