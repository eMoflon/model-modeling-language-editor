package de.nexus.emml.generator.entities.model;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Dataclass for a package
 */
public class PackageEntity {
	private String referenceId;
	private String name;
	private ArrayList<AbstractClassEntity> abstractClasses;
	private ArrayList<EnumEntity<?>> enums;
	private ArrayList<PackageEntity> subPackages;

	public String getReferenceId() {
		return referenceId;
	}

	public String getName() {
		return name;
	}

	public ArrayList<AbstractClassEntity> getAbstractClasses() {
		return abstractClasses;
	}

	public ArrayList<EnumEntity<?>> getEnums() {
		return enums;
	}

	public ArrayList<PackageEntity> getSubPackages() {
		return subPackages;
	}

	@Override
	public String toString() {
		String classesString = abstractClasses.size() == 0 ? ""
				: "\n" + abstractClasses.stream().map(AbstractClassEntity::toString).collect(Collectors.joining(","))
						+ "\n";
		String enumsString = enums.size() == 0 ? ""
				: "\n" + enums.stream().map(EnumEntity::toString).collect(Collectors.joining(",")) + "\n";
		String subPackagesString = subPackages.size() == 0 ? ""
				: "\n" + subPackages.stream().map(PackageEntity::toString).collect(Collectors.joining(",")) + "\n";
		return String.format("%s{%s %s %s}", name, classesString, enumsString, subPackagesString);
	}
}
