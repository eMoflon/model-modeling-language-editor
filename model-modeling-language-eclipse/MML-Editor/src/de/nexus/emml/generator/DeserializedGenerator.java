package de.nexus.emml.generator;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Platform;

public class DeserializedGenerator {
	private final ArrayList<GeneratorEntry> generatorStorage;
	private final ArrayList<DiagnosticEntry> diagnosticStorage;

	public DeserializedGenerator(ArrayList<GeneratorEntry> gen, ArrayList<DiagnosticEntry> dia) {
		this.generatorStorage = gen.stream().filter(x -> x.getUri().getScheme().equalsIgnoreCase("file"))
				.collect(Collectors.toCollection(ArrayList::new));
		this.diagnosticStorage = dia.stream().filter(x -> x.getUri().getScheme().equalsIgnoreCase("file"))
				.collect(Collectors.toCollection(ArrayList::new));

		for (GeneratorEntry entry : generatorStorage) {
			Platform.getLog(getClass())
					.info("[POST] Gen path: " + entry.getUri().getScheme() + " | " + entry.getUri().getPath());
		}
	}

	public boolean hasErrors() {
		return this.diagnosticStorage.size() != 0;
	}

	public ArrayList<GeneratorEntry> getGeneratorStorage() {
		return generatorStorage;
	}

	public ArrayList<DiagnosticEntry> getDiagnosticStorage() {
		return diagnosticStorage;
	}
}
