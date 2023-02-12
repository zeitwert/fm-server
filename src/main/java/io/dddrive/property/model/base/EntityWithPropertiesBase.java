package io.dddrive.property.model.base;

import java.util.HashMap;
import java.util.Map;

import io.dddrive.property.model.Property;
import io.dddrive.property.model.impl.EnumSetPropertyImpl;
import io.dddrive.property.model.impl.PartListPropertyImpl;
import io.dddrive.property.model.impl.ReferenceSetPropertyImpl;

public abstract class EntityWithPropertiesBase implements EntityWithPropertiesMixin {

	private Map<String, Property<?>> propertyMap = new HashMap<>();

	@Override
	public Map<String, Property<?>> propertyMap() {
		return this.propertyMap;
	}

	protected void doBeforeStoreProperties() {
		for (Property<?> p : this.getProperties()) {
			if (p instanceof EnumSetPropertyImpl<?>) {
				((EnumSetPropertyImpl<?>) p).doBeforeStore();
			} else if (p instanceof ReferenceSetPropertyImpl<?>) {
				((ReferenceSetPropertyImpl<?>) p).doBeforeStore();
			} else if (p instanceof PartListPropertyImpl<?>) {
				((PartListPropertyImpl<?>) p).doBeforeStore();
			}
		}
	}

}
