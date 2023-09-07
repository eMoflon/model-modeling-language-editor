package de.nexus.emml.generator.entities.model;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class AbstractClassEntity {
	private String referenceId;
	private String name;
	private boolean isAbstract;
	private boolean isInterface;
	private ArrayList<AttributeEntity<?>> attributes = new ArrayList<AttributeEntity<?>>();
	private ArrayList<CReferenceEntity> references = new ArrayList<CReferenceEntity>();
	private ArrayList<String> extendsIds = new ArrayList<String>();
	private ArrayList<String> implementsIds = new ArrayList<String>();

	public String getReferenceId() {
		return referenceId;
	}

	public String getName() {
		return name;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public boolean isInterface() {
		return isInterface;
	}

	public ArrayList<AttributeEntity<?>> getAttributes() {
		return attributes;
	}

	public ArrayList<CReferenceEntity> getReferences() {
		return references;
	}

	public ArrayList<String> getExtendsIds() {
		return extendsIds;
	}

	public ArrayList<String> getImplementsIds() {
		return implementsIds;
	}

	@Override
	public String toString() {
		String attributeString = attributes.size() == 0 ? ""
				: "\n" + attributes.stream().map(AttributeEntity::toString).collect(Collectors.joining(",")) + "\n";
		String referenceString = references.size() == 0 ? ""
				: "\n" + references.stream().map(CReferenceEntity::toString).collect(Collectors.joining(",")) + "\n";
		return String.format("%s(isAbstract:%b|isInterface:%b||%s||%s)", name, isAbstract, isInterface, attributeString,
				referenceString);
	}
}
