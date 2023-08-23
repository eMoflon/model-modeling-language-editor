package de.nexus.emml.generator.entities;

public class EnumEntryEntity<T> {
	private String referenceId;
	private String name;
	private boolean hasDefaultValue;
	private T defaultValue;

	public String getReferenceId() {
		return referenceId;
	}

	public String getName() {
		return name;
	}

	public boolean isHasDefaultValue() {
		return hasDefaultValue;
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String toString() {
		return String.format("[%s|%b]", name, hasDefaultValue);
	}
}
