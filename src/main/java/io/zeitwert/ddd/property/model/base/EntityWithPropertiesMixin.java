package io.zeitwert.ddd.property.model.base;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.List;
import java.util.Map;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.enums.model.Enumerated;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.EntityWithProperties;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.EnumSetProperty;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.PropertyProvider;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.ReferenceSetProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.impl.PartListPropertyImpl;
import io.zeitwert.ddd.property.model.wrapper.EnumPropertyWrapper;
import io.zeitwert.ddd.property.model.wrapper.EnumSetPropertyWrapper;
import io.zeitwert.ddd.property.model.wrapper.PartListPropertyWrapper;
import io.zeitwert.ddd.property.model.wrapper.ReferencePropertyWrapper;
import io.zeitwert.ddd.property.model.wrapper.ReferenceSetPropertyWrapper;
import io.zeitwert.ddd.property.model.wrapper.SimplePropertyWrapper;

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

	default <P extends Part<?>> PartListProperty<P> addPartListProperty(CodePartListType partListType) {
		requireThis(partListType != null, "partListType not null");
		PartListProperty<P> property = new PartListPropertyImpl<>(this, partListType);
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
