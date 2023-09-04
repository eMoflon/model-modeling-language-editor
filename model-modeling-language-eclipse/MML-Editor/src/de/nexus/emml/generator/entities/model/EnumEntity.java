package de.nexus.emml.generator.entities.model;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class EnumEntity<T> {
	private String referenceId;
	private String name;
	private String type;
	private ArrayList<EnumEntryEntity<T>> entries;

	public String getReferenceId() {
		return referenceId;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public ArrayList<EnumEntryEntity<T>> getEntries() {
		return entries;
	}

	@Override
	public String toString() {
		return String.format("ENUM<%s,%s>{%s}", name, type, entries.size() == 0 ? ""
				: entries.stream().map(EnumEntryEntity::toString).collect(Collectors.joining(",")));
	}
}