
package io.zeitwert.ddd.app.service.api.impl;

import io.zeitwert.ddd.enums.model.Enumerated;
import io.zeitwert.ddd.enums.model.Enumeration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service("enumerations")
public final class Enumerations {

	private Map<String, Enumeration<? extends Enumerated>> enumsById = new HashMap<>();
	private Map<Class<? extends Enumerated>, Enumeration<? extends Enumerated>> enumsByEnumeratedClass = new HashMap<>();
	private Map<Class<? extends Enumeration<?>>, Enumeration<? extends Enumerated>> enumsByEnumerationClass = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <E extends Enumerated> void addEnumeration(Class<E> enumeratedClass, Enumeration<E> e) {
		this.enumsById.put(e.getModule() + "." + e.getId(), e);
		this.enumsByEnumerationClass.put((Class<Enumeration<E>>) e.getClass(), e);
		this.enumsByEnumeratedClass.put(enumeratedClass, e);
	}

	public Enumeration<? extends Enumerated> getEnumeration(String module, String name) {
		return this.enumsById.get(module + "." + name + "Enum");
	}

	@SuppressWarnings("unchecked")
	public <E extends Enumeration<? extends Enumerated>> E getEnumerationByEnumeration(Class<E> enumClass) {
		return (E) this.enumsByEnumerationClass.get(enumClass);
	}

	@SuppressWarnings("unchecked")
	public <E extends Enumerated> Enumeration<E> getEnumeration(Class<E> enumClass) {
		return (Enumeration<E>) this.enumsByEnumeratedClass.get(enumClass);
	}

}
