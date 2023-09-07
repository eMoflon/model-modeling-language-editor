package de.nexus.emml.generator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import de.nexus.emml.EditorActivator;
import de.nexus.emml.generator.entities.instance.GeneratorInstance;
import de.nexus.emml.generator.entities.model.ModelEntity;
import de.nexus.emml.generator.entities.model.PackageEntity;

public class EmfResourceBuilder {
	public static void buildEmfResources(ArrayList<GeneratorEntry> generators, File basePath) {
		Platform.getLog(EditorActivator.getDefault().getBundle()).info("Deserialize model");
		ModelEntity modEntity = generators.get(0).getModel();
		Platform.getLog(EditorActivator.getDefault().getBundle()).info("==========[Building resources]==========");
		List<EcoreTypeGraphBuilder> typeBuilders = new ArrayList<>();
		List<XMIInstanceGraphBuilder> instanceBuilders = new ArrayList<>();
		EcoreTypeResolver typeResolver = new EcoreTypeResolver();
		XMIInstanceResolver instanceResolver = new XMIInstanceResolver();
		// Obtain a new resource set
		ResourceSet resSet = new ResourceSetImpl();
		for (GeneratorEntry genEntry : generators) {
			String projectName = Path.of(genEntry.getUri()).subpath(0, 1).toString();
			Path modelsDir = Paths.get(basePath.toString(), projectName, "model");
			try {
				Files.createDirectories(modelsDir);
			} catch (IOException ex) {
				Platform.getLog(EditorActivator.getDefault().getBundle())
						.info("[MODEL PATH BUILDER] Could not create models directory: " + modelsDir.toString());
			}
			ModelEntity model = genEntry.getModel();
			for (PackageEntity pckgEntity : model.getPackages()) {
				String fileName = Path.of(genEntry.getUri()).getFileName().toString().replace(".mml", "") + "_"
						+ pckgEntity.getName() + ".ecore";
				Path filePath = Paths.get(modelsDir.toString(), fileName);
				String packageUri = String.format("platform:/resource/%s/model/%s", projectName, fileName);
				typeBuilders.add(new EcoreTypeGraphBuilder(pckgEntity, packageUri, filePath.toString(), typeResolver));
				Platform.getLog(EditorActivator.getDefault().getBundle())
						.info(String.format("	- %s [EXPORTED to %s]", pckgEntity.getName(), filePath.toString()));
			}
		}
		EcoreTypeGraphBuilder.buildEcoreFile(typeBuilders, typeResolver,resSet);
		Platform.getLog(EditorActivator.getDefault().getBundle()).info("Ecore created!");
		
		for (GeneratorEntry genEntry : generators) {
			String projectName = Path.of(genEntry.getUri()).subpath(0, 1).toString();
			Path modelsDir = Paths.get(basePath.toString(), projectName, "model");
			try {
				Files.createDirectories(modelsDir);
			} catch (IOException ex) {
				Platform.getLog(EditorActivator.getDefault().getBundle())
						.info("[MODEL PATH BUILDER] Could not create models directory: " + modelsDir.toString());
			}
			
			for (GeneratorInstance instWrapper : genEntry.getInstances().getSerializedInstances()) {
				String fileName = Path.of(genEntry.getUri()).getFileName().toString().replace(".mml", "") + "_"
						+ instWrapper.getInstanceName() + ".xmi";
				Path filePath = Paths.get(modelsDir.toString(), fileName);
				instanceBuilders.add(new XMIInstanceGraphBuilder(instWrapper.getInstances(), filePath.toString(), typeResolver,instanceResolver));
				Platform.getLog(EditorActivator.getDefault().getBundle())
						.info(String.format("	- %s [EXPORTED to %s]", instWrapper.getInstanceName(), filePath.toString()));
			}
		}
		XMIInstanceGraphBuilder.buildXmiFile(instanceBuilders, typeResolver,instanceResolver, resSet);
		Platform.getLog(EditorActivator.getDefault().getBundle()).info("XMI created!");
	}
}
