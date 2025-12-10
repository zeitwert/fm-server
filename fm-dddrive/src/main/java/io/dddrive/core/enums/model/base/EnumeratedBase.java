
package io.dddrive.core.enums.model.base;

import io.dddrive.core.enums.model.Enumerated;
import io.dddrive.core.enums.model.Enumeration;

public abstract class EnumeratedBase implements Enumerated {

	private Enumeration<? extends Enumerated> enumeration;

	private String id;

	private String name;

	public EnumeratedBase(Enumeration<? extends Enumerated> enumeration, String id, String name) {
		this.enumeration = enumeration;
		this.id = id;
		this.name = name;
	}

	@Override
	public Enumeration<? extends Enumerated> getEnumeration() {
		return this.enumeration;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[" + this.getId() + "]";
	}

}
