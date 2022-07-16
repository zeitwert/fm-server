
package io.zeitwert.ddd.app.service.api.impl;

import io.zeitwert.ddd.enums.model.Enumerated;
import io.zeitwert.ddd.enums.model.Enumeration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service("enumerations")
public final class Enumerations {

	private Map<String, Enumeration<? extends Enumerated>> enumsById = new HashMap<String, Enumeration<? extends Enumerated>>();
	private Map<Class<?>, Enumeration<? extends Enumerated>> enumsByClass = new HashMap<Class<?>, Enumeration<? extends Enumerated>>();

	public void addEnumeration(final Enumeration<? extends Enumerated> e) {
		this.enumsById.put(e.getModule() + "." + e.getId(), e);
		this.enumsByClass.put(e.getClass(), e);
	}

	public Enumeration<? extends Enumerated> getEnumeration(final String module, final String name) {
		return this.enumsById.get(module + "." + name + "Enum");
	}

	@SuppressWarnings("unchecked")
	public <E extends Enumeration<? extends Enumerated>> E getEnumeration(final Class<E> enumClass) {
		return (E) this.enumsByClass.get(enumClass);
	}

}
