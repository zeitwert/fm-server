package io.zeitwert.ddd.property.model.base;

import javassist.util.proxy.MethodFilter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class PropertyFilter implements MethodFilter {

	public static final PropertyFilter INSTANCE = new PropertyFilter();

	@Override
	public boolean isHandled(Method method) {
		return Modifier.isAbstract(method.getModifiers());
	}

}
