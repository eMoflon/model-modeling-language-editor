package de.nexus.emml.generator.entities;

import com.google.gson.annotations.SerializedName;

public class ClassElementModifiers {
	private boolean readonly;
	@SerializedName("volatile")
	private boolean _volatile;
	@SerializedName("transient")
	private boolean _transient;
	private boolean unsettable;
	private boolean derived;
	private boolean unique;
	private boolean ordered;
	private boolean resolve;
	private boolean id;

	public boolean isReadonly() {
		return readonly;
	}

	public boolean isVolatile() {
		return _volatile;
	}

	public boolean isTransient() {
		return _transient;
	}

	public boolean isUnsettable() {
		return unsettable;
	}

	public boolean isDerived() {
		return derived;
	}

	public boolean isUnique() {
		return unique;
	}

	public boolean isOrdered() {
		return ordered;
	}

	public boolean isResolve() {
		return resolve;
	}

	public boolean isId() {
		return id;
	}

}
