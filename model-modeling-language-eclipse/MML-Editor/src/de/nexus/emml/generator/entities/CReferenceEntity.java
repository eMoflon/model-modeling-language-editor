package de.nexus.emml.generator.entities;

public class CReferenceEntity {
	private String referenceId;
	private String name;
	private MultiplicityEntity multiplicity;
	private String type;
	private ClassElementModifiers modifiers;
	private boolean hasOpposite;
	private String opposite;

	public String getReferenceId() {
		return referenceId;
	}

	public String getName() {
		return name;
	}

	public MultiplicityEntity getMultiplicity() {
		return multiplicity;
	}

	public String getType() {
		return type;
	}

	public ClassElementModifiers getModifiers() {
		return modifiers;
	}

	public boolean isHasOpposite() {
		return hasOpposite;
	}

	public String getOpposite() {
		return opposite;
	}

	@Override
	public String toString() {
		return String.format("(%s -> %s)", name, type);
	}

}
