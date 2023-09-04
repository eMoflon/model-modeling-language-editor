package de.nexus.emml.generator;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcorePackage;

public class EmfGraphBuilderUtils {
	public static EDataType mapETypes(String mmlType) {
		return switch (mmlType) {
			case "string" -> EcorePackage.Literals.ESTRING;
			case "float" -> EcorePackage.Literals.EFLOAT;
			case "double" -> EcorePackage.Literals.EDOUBLE;
			case "int" -> EcorePackage.Literals.EINT;
			case "boolean" -> EcorePackage.Literals.EBOOLEAN;
			default -> EcorePackage.Literals.ESTRING;
		};
	}
	
	public static <T,R> R mapVals(String mmlType, T value) {
		return mapVals(mapETypes(mmlType), value);
	}
	
	@SuppressWarnings("unchecked")
	public static <T,R> R mapVals(EDataType type, T value) {
		return switch (type.getClassifierID()) {
			case EcorePackage.ESTRING -> (R) value;
			case EcorePackage.EFLOAT -> (R) Float.valueOf(value.toString());
			case EcorePackage.EDOUBLE -> (R) Double.valueOf(value.toString());
			case EcorePackage.EINT -> (R) Integer.valueOf(Double.valueOf(value.toString()).intValue());
			case EcorePackage.EBOOLEAN -> (R) Boolean.valueOf(value.toString());
			default -> (R) value.toString();
		};
	}
}
