package de.nexus.emml.generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import de.nexus.emml.generator.entities.AbstractClassEntity;
import de.nexus.emml.generator.entities.AttributeEntity;
import de.nexus.emml.generator.entities.CReferenceEntity;
import de.nexus.emml.generator.entities.PackageEntity;

public class EcoreTypeGraphBuilder {
	private final EPackage ePackage;
	private final String exportPath;
	
	public EcoreTypeGraphBuilder(PackageEntity pckg, String targetUri, String exportPath) {
		this.ePackage = createPackage(pckg.getName(), pckg.getName(), targetUri);
		this.exportPath = exportPath;
		pckg.getSubPackages().forEach(subPckg -> {
			EPackage subPackage = new EcoreTypeGraphBuilder(subPckg, targetUri).getAsSubpackage();
			this.ePackage.getESubpackages().add(subPackage);
		});
		pckg.getAbstractClasses().forEach(ab -> {
			EClass clss = createEClass(ab);
			ab.getAttributes().forEach(attr -> addAttribute(clss, attr, false));
			ab.getReferences().forEach(cref -> addReference(clss, cref));
		});
	}
	
	public EcoreTypeGraphBuilder(PackageEntity pckg, String targetUri) {
		this(pckg,targetUri, null);
	}
	
	public static void buildEcoreFile(List<EcoreTypeGraphBuilder> graphBuilderList) {
		for (EcoreTypeGraphBuilder builder : graphBuilderList) {
			builder.ePackage.eClass();
		}
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put(EcorePackage.eNAME, new XMIResourceFactoryImpl());

		// Obtain a new resource set
		ResourceSet resSet = new ResourceSetImpl();
		List<Resource> resources = new ArrayList();
		// create a resource
		try {
			for (EcoreTypeGraphBuilder builder : graphBuilderList) {
				Resource resource = resSet.createResource(URI.createFileURI(Objects.requireNonNull(builder.exportPath)));
				/*
				 * add your EPackage as root, everything is hierarchical included in this first
				 * node
				 */
				resource.getContents().add(builder.ePackage);
				resources.add(resource);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		// now save the content.
		for (Resource resource : resources) {
			try {
				resource.save(Collections.EMPTY_MAP);
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}

	public void toEcoreFile(String path) {
		/* Initialize your EPackage */
		ePackage.eClass();
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		/* add default .ecore extension for ecore file */
		m.put(EcorePackage.eNAME, new XMIResourceFactoryImpl());

		// Obtain a new resource set
		ResourceSet resSet = new ResourceSetImpl();
		// create a resource
		Resource resource = null;
		try {
			resource = resSet.createResource(URI.createFileURI(path));
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * add your EPackage as root, everything is hierarchical included in this first
		 * node
		 */
		resource.getContents().add(ePackage);

		// now save the content.
		try {
			resource.save(Collections.EMPTY_MAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public EPackage getAsSubpackage() {
		return this.ePackage;
	}

	private void addAttribute(EClass containerClass, AttributeEntity attr, boolean isId) {
		final EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		// always add to container first
		containerClass.getEStructuralFeatures().add(attribute);
		attribute.setName(attr.getName());
		attribute.setEType(mapETypes(attr.getType()));
		attribute.setID(isId);
		attribute.setLowerBound(0);
		attribute.setUpperBound(1);
		
		attribute.setOrdered(attr.getModifiers().isOrdered());
		attribute.setTransient(attr.getModifiers().isTransient());
		attribute.setUnique(attr.getModifiers().isUnique());
		attribute.setUnsettable(attr.getModifiers().isUnsettable());
		attribute.setVolatile(attr.getModifiers().isVolatile());
		attribute.setChangeable(!attr.getModifiers().isReadonly());
	}

	private void addReference(EClass containerClass, CReferenceEntity cref) {
		final EReference reference = EcoreFactory.eINSTANCE.createEReference();
		// always add to container first
		containerClass.getEStructuralFeatures().add(reference);
		reference.setName(cref.getName());

		// TODO: Set correct values
		reference.setEType(EcorePackage.Literals.ESTRING);
		reference.setLowerBound(0);
		reference.setUpperBound(1);
		
		reference.setChangeable(!cref.getModifiers().isReadonly());
		reference.setVolatile(cref.getModifiers().isVolatile());
		reference.setUnsettable(cref.getModifiers().isUnsettable());
		reference.setUnique(cref.getModifiers().isUnique());
		reference.setTransient(cref.getModifiers().isTransient());
		reference.setOrdered(cref.getModifiers().isOrdered());
		reference.setResolveProxies(cref.getModifiers().isResolve());
	}

	private EPackage createPackage(final String name, final String prefix, final String uri) {
		final EPackage epackage = EcoreFactory.eINSTANCE.createEPackage();
		epackage.setName(name);
		epackage.setNsPrefix(prefix);
		epackage.setNsURI(uri);
		return epackage;

	}

	private EClass createEClass(final AbstractClassEntity ace) {
		final EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName(ace.getName());
		eClass.setAbstract(ace.isAbstract());
		eClass.setInterface(ace.isInterface());
		this.ePackage.getEClassifiers().add(eClass);
		return eClass;
	}
	
	private EDataType mapETypes(String mmlType) {
		return switch (mmlType) {
			case "string" -> EcorePackage.Literals.ESTRING;
			case "float" -> EcorePackage.Literals.EFLOAT;
			case "double" -> EcorePackage.Literals.EDOUBLE;
			case "int" -> EcorePackage.Literals.EINT;
			case "boolean" -> EcorePackage.Literals.EBOOLEAN;
			default -> EcorePackage.Literals.ESTRING;
		};
	}
}
