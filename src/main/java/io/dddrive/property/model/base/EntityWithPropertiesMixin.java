package io.dddrive.property.model.base;

import static io.dddrive.util.Invariant.requireThis;

import java.util.List;
import java.util.Map;

import io.dddrive.ddd.model.Aggregate;
import io.dddrive.ddd.model.Part;
import io.dddrive.enums.model.Enumerated;
import io.dddrive.property.model.EntityWithProperties;
import io.dddrive.property.model.EnumProperty;
import io.dddrive.property.model.EnumSetProperty;
import io.dddrive.property.model.PartListProperty;
import io.dddrive.property.model.Property;
import io.dddrive.property.model.PropertyProvider;
import io.dddrive.property.model.ReferenceProperty;
import io.dddrive.property.model.ReferenceSetProperty;
import io.dddrive.property.model.SimpleProperty;
import io.dddrive.property.model.wrapper.EnumPropertyWrapper;
import io.dddrive.property.model.wrapper.EnumSetPropertyWrapper;
import io.dddrive.property.model.wrapper.PartListPropertyWrapper;
import io.dddrive.property.model.wrapper.ReferencePropertyWrapper;
import io.dddrive.property.model.wrapper.ReferenceSetPropertyWrapper;
import io.dddrive.property.model.wrapper.SimplePropertyWrapper;

public interface EntityWithPropertiesMixin extends EntityWithProperties, EntityWithPropertiesSPI {

	Map<String, Property<?>> propertyMap();

	@Override
	PropertyProvider getPropertyProvider();

	@Override
	default boolean hasProperty(String name) {
		return this.propertyMap().containsKey(name);
	}

	@Override
	default Property<?> getProperty(String name) {
		return this.propertyMap().get(name);
	}

	@Override
	default List<Property<?>> getProperties() {
		return this.propertyMap().values().stream().toList();
	}

	default void addProperty(Property<?> property) {
		requireThis(property.getName() != null, "property has name");
		requireThis(!this.hasProperty(property.getName()), "property [" + property.getName() + "] is unique");
		this.propertyMap().put(property.getName(), property);
	}

	default <T> SimpleProperty<T> addSimpleProperty(String name, Class<T> type) {
		SimpleProperty<T> property;
		if (this.getPropertyProvider() == null) {
			property = new SimplePropertyWrapper<>(this, name, type);
		} else {
			property = this.getPropertyProvider().getSimpleProperty(this, name, type);
		}
		this.addProperty(property);
		return property;
	}

	default <E extends Enumerated> EnumProperty<E> addEnumProperty(String name, Class<E> type) {
		EnumProperty<E> property;
		if (this.getPropertyProvider() == null) {
			property = new EnumPropertyWrapper<>(this, name, type);
		} else {
			property = this.getPropertyProvider().getEnumProperty(this, name, type);
		}
		this.addProperty(property);
		return property;
	}

	default <E extends Enumerated> EnumSetProperty<E> addEnumSetProperty(String name, Class<E> type) {
		EnumSetProperty<E> property;
		if (this.getPropertyProvider() == null) {
			property = new EnumSetPropertyWrapper<>(this, name, type);
		} else {
			property = this.getPropertyProvider().getEnumSetProperty(this, name, type);
		}
		this.addProperty(property);
		return property;
	}

	default <A extends Aggregate> ReferenceProperty<A> addReferenceProperty(String name, Class<A> type) {
		ReferenceProperty<A> property = null;
		if (this.getPropertyProvider() == null) {
			property = new ReferencePropertyWrapper<>(this, name, type);
		} else {
			property = this.getPropertyProvider().getReferenceProperty(this, name, type);
		}
		this.addProperty(property);
		return property;
	}

	default <A extends Aggregate> ReferenceSetProperty<A> addReferenceSetProperty(String name, Class<A> type) {
		ReferenceSetProperty<A> property;
		if (this.getPropertyProvider() == null) {
			property = new ReferenceSetPropertyWrapper<>(this, name, type);
		} else {
			property = this.getPropertyProvider().getReferenceSetProperty(this, name, type);
		}
		this.addProperty(property);
		return property;
	}

	default <P extends Part<?>> PartListProperty<P> addPartListProperty(String name, Class<P> type) {
		PartListProperty<P> property;
		if (this.getPropertyProvider() == null) {
			property = new PartListPropertyWrapper<>(this, name, type);
		} else {
			property = this.getPropertyProvider().getPartListProperty(this, name, type);
		}
		this.addProperty(property);
		return property;
	}

}
