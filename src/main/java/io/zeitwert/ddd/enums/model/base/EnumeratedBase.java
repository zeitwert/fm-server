
package io.zeitwert.ddd.enums.model.base;

import io.zeitwert.ddd.enums.model.Enumerated;
import io.zeitwert.ddd.enums.model.Enumeration;

import java.util.Objects;

public class EnumeratedBase implements Enumerated {

	private Enumeration<? extends Enumerated> enumeration;
	private String id;
	private String name;

	protected EnumeratedBase(Enumeration<? extends Enumerated> enumeration, String id, String name) {
		this.enumeration = enumeration;
		this.id = id;
		this.name = name;
	}

	public Enumeration<? extends Enumerated> getEnumeration() {
		return this.enumeration;
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null) {
			return false;
		}
		if (getClass() != object.getClass()) {
			return false;
		}
		EnumeratedBase enumerated = (EnumeratedBase) object;
		return Objects.equals(this.name, enumerated.name);
	}

	public String toString() {
		return this.getClass() + "(" + this.getId() + ", " + this.getName() + ")";
	}

}
