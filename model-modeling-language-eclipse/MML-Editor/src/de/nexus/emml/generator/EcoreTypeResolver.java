package de.nexus.emml.generator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;

import de.nexus.emml.generator.entities.instance.AttributeEntry;
import de.nexus.emml.generator.entities.instance.ObjectInstance;
import de.nexus.emml.generator.entities.instance.ReferenceEntry;
import de.nexus.emml.generator.entities.model.AttributeEntity;
import de.nexus.emml.generator.entities.model.CReferenceEntity;

public class EcoreTypeResolver {
	private final Map<String, EClassifier> classifiers = new HashMap<String, EClassifier>();
	private final Map<String, EReference> references = new HashMap<String, EReference>();
	private final Map<String, EAttribute> attributes = new HashMap<String, EAttribute>();
	private final Map<String, EPackage> packages = new HashMap<String, EPackage>();
	private final Map<String, EEnumLiteral> elits = new HashMap<String, EEnumLiteral>();
	private final Map<EReference, String> unresolvedReferenceTypes = new HashMap<EReference, String>();
	private final Map<EReference, String> unresolvedReferenceOpposites = new HashMap<EReference, String>();
	private final Map<EAttribute, String> unresolvedAttributeEnumTypes = new HashMap<EAttribute, String>();
	private final Map<EAttribute, String> unresolvedAttributeEnumValues = new HashMap<EAttribute, String>();
	private final Map<EClass, List<String>> unresolvedSupertypes = new HashMap<EClass, List<String>>();

	public void store(String classId, EClassifier classifier) {
		this.classifiers.put(classId, classifier);
	}

	public void store(String refId, EReference reference) {
		this.references.put(refId, reference);
	}

	public void store(String attrId, EAttribute attibute) {
		this.attributes.put(attrId, attibute);
	}

	public void store(String pckgId, EPackage packagee) {
		this.packages.put(pckgId, packagee);
	}

	public void store(String elitId, EEnumLiteral enumLiteral) {
		this.elits.put(elitId, enumLiteral);
	}

	public void dumpResolverStorage() {
		Platform.getLog(getClass()).info("================[ERROR]================");
		Platform.getLog(getClass()).info("==========[EcoreTypeResolver]==========");
		Platform.getLog(getClass()).info("### Packages");
		for (String key : this.packages.keySet()) {
			Platform.getLog(getClass()).info(String.format("	- %s", key));
		}
		Platform.getLog(getClass()).info("### Classifiers");
		for (String key : this.classifiers.keySet()) {
			Platform.getLog(getClass()).info(String.format("	- %s", key));
		}
		Platform.getLog(getClass()).info("### Attributes");
		for (String key : this.attributes.keySet()) {
			Platform.getLog(getClass()).info(String.format("	- %s", key));
		}
		Platform.getLog(getClass()).info("### References");
		for (String key : this.references.keySet()) {
			Platform.getLog(getClass()).info(String.format("	- %s", key));
		}
		Platform.getLog(getClass()).info("### ELits");
		for (String key : this.elits.keySet()) {
			Platform.getLog(getClass()).info(String.format("	- %s", key));
		}
		Platform.getLog(getClass()).info("================[ERROR]================");
	}

	public void resolveSupertypes(EClass clazz, List<String> classIds) {
		this.unresolvedSupertypes.put(clazz, classIds);
	}

	public void resolveReference(EReference ref, CReferenceEntity refEntity) {
		String typeId = refEntity.getType();
		if (classifiers.containsKey(typeId)) {
			ref.setEType(classifiers.get(typeId));
		} else {
			unresolvedReferenceTypes.put(ref, typeId);
		}

		if (refEntity.isHasOpposite()) {
			String oppositeId = refEntity.getOpposite();
			if (classifiers.containsKey(oppositeId)) {
				ref.setEType(classifiers.get(oppositeId));
			} else {
				unresolvedReferenceOpposites.put(ref, oppositeId);
			}
		}
	}

	public void resolveAttributeEnum(EAttribute attr, AttributeEntity<String> attrEntity) {
		if (!attrEntity.isEnumType()) {
			return;
		}

		String typeId = attrEntity.getType();
		if (classifiers.containsKey(typeId)) {
			attr.setEType(classifiers.get(typeId));
		} else {
			unresolvedAttributeEnumTypes.put(attr, typeId);
		}

		if (attrEntity.isHasDefaultValue()) {
			String valueId = attrEntity.getDefaultValue();
			if (elits.containsKey(valueId)) {
				attr.setDefaultValue(elits.get(valueId).getName());
			} else {
				unresolvedAttributeEnumValues.put(attr, valueId);
			}
		}
	}

	public EObject resolveObjectInstance(ObjectInstance objInst) {
		EClass clazz = (EClass) this.classifiers.get(objInst.getReferenceTypeId());
		return EcoreUtil.create(clazz);
	}

	public EAttribute resolveAttribute(AttributeEntry<?> attr) {
		return this.attributes.get(attr.getTypeId());
	}

	public EReference resolveReference(ReferenceEntry ref) {
		return this.references.get(ref.getTypeId());
	}

	public EEnumLiteral resolveAttributeEnum(AttributeEntry<?> attr) {
		return this.elits.get(attr.getValue());
	}

	public void resolveUnresovedTypes() {
		for (Map.Entry<EReference, String> refEntry : this.unresolvedReferenceTypes.entrySet()) {
			EReference ref = refEntry.getKey();
			String typeId = refEntry.getValue();
			if (classifiers.containsKey(typeId)) {
				ref.setEType(classifiers.get(typeId));
			} else {
				dumpResolverStorage();
				throw new IllegalArgumentException("Could not resolve classId: " + typeId);
			}
		}

		for (Map.Entry<EReference, String> refEntry : this.unresolvedReferenceOpposites.entrySet()) {
			EReference ref = refEntry.getKey();
			String typeId = refEntry.getValue();
			if (references.containsKey(typeId)) {
				ref.setEOpposite(references.get(typeId));
			} else {
				dumpResolverStorage();
				throw new IllegalArgumentException("Could not resolve referenceId: " + typeId);
			}
		}

		for (Map.Entry<EAttribute, String> attrEntry : this.unresolvedAttributeEnumTypes.entrySet()) {
			EAttribute attr = attrEntry.getKey();
			String typeId = attrEntry.getValue();
			if (classifiers.containsKey(typeId)) {
				attr.setEType(classifiers.get(typeId));
			} else {
				dumpResolverStorage();
				throw new IllegalArgumentException("Could not resolve enumId: " + typeId);
			}
		}

		for (Map.Entry<EAttribute, String> attrEntry : this.unresolvedAttributeEnumValues.entrySet()) {
			EAttribute attr = attrEntry.getKey();
			String typeId = attrEntry.getValue();
			if (elits.containsKey(typeId)) {
				attr.setDefaultValue(elits.get(typeId).getName());
			} else {
				dumpResolverStorage();
				throw new IllegalArgumentException("Could not resolve enum valueId: " + typeId);
			}
		}

		for (Map.Entry<EClass, List<String>> supertypeEntry : this.unresolvedSupertypes.entrySet()) {
			EClass clazz = supertypeEntry.getKey();
			List<String> supertypeIds = supertypeEntry.getValue();
			EList<EClass> superTypes = clazz.getESuperTypes();
			for (String supertypeId : supertypeIds) {
				if (classifiers.containsKey(supertypeId)) {
					superTypes.add((EClass) classifiers.get(supertypeId));
				} else {
					dumpResolverStorage();
					throw new IllegalArgumentException("Could not resolve supertype classId: " + supertypeId);
				}
			}
		}
	}
}
