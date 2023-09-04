package de.nexus.emml.generator.entities.model;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ModelEntity {
	private ArrayList<PackageEntity> packages = new ArrayList<>();

	public ArrayList<PackageEntity> getPackages() {
		return packages;
	}

	@Override
	public String toString() {
		return packages.size() == 0 ? ""
				: "\n" + packages.stream().map(PackageEntity::toString).collect(Collectors.joining(",")) + "\n";
	}
}
