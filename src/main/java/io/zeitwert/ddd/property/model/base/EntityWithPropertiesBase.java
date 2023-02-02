package io.zeitwert.ddd.property.model.base;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.Field;
import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.service.api.AggregateCache;
import io.zeitwert.ddd.enums.model.Enumerated;
import io.zeitwert.ddd.enums.model.Enumeration;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.persistence.jooq.impl.EnumPropertyImpl;
import io.zeitwert.ddd.persistence.jooq.impl.ReferencePropertyImpl;
import io.zeitwert.ddd.persistence.jooq.impl.SimplePropertyImpl;
import io.zeitwert.ddd.property.model.EntityWithProperties;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.EnumSetProperty;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.ReferenceSetProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.impl.EnumSetPropertyImpl;
import io.zeitwert.ddd.property.model.impl.PartListPropertyImpl;
import io.zeitwert.ddd.property.model.impl.ReferenceSetPropertyImpl;
import io.zeitwert.ddd.property.model.wrapper.EnumPropertyWrapper;
import io.zeitwert.ddd.property.model.wrapper.EnumSetPropertyWrapper;
import io.zeitwert.ddd.property.model.wrapper.PartListPropertyWrapper;
import io.zeitwert.ddd.property.model.wrapper.ReferencePropertyWrapper;
import io.zeitwert.ddd.property.model.wrapper.ReferenceSetPropertyWrapper;
import io.zeitwert.ddd.property.model.wrapper.SimplePropertyWrapper;

public abstract class EntityWithPropertiesBase implements EntityWithProperties, EntityWithPropertiesSPI {

	private Map<String, Property<?>> propertyMap = new HashMap<>();
	private List<Property<?>> properties = new ArrayList<>();

	@Override
	public boolean hasProperty(String name) {
		return this.propertyMap.containsKey(name);
	}

	@Override
	public Property<?> getProperty(String name) {
		return this.propertyMap.get(name);
	}

	@Override
	public List<Property<?>> getProperties() {
		return List.copyOf(this.properties);
	}

	// @Override
	// public Part<?> addPart(Property<?> property, CodePartListType partListType) {
	// throw new NoSuchMethodError(
	// this.getClass().getSimpleName() + ".addPart() [" + property.getName() + ", "
	// + partListType + "]");
	// }

	protected void addProperty(Property<?> property) {
		requireThis(property.getName() != null, "property has name");
		requireThis(!this.hasProperty(property.getName()), "property [" + property.getName() + "] is unique");
		this.propertyMap.put(property.getName(), property);
		this.properties.add(property);
	}

	protected <T> SimpleProperty<T> addSimpleProperty(UpdatableRecord<?> dbRecord, Field<T> field) {
		SimpleProperty<T> property = new SimplePropertyImpl<>(this, dbRecord, field);
		this.addProperty(property);
		return property;
	}

	protected <T> SimpleProperty<T> addSimpleProperty(String name, Class<T> type) {
		SimpleProperty<T> property;
		if (this.getPropertyProvider() == null) {
			property = new SimplePropertyWrapper<>(this, name, type);
		} else {
			property = this.getPropertyProvider().getSimpleProperty(this, name, type);
		}
		this.addProperty(property);
		return property;
	}

	protected <E extends Enumerated> EnumProperty<E> addEnumProperty(UpdatableRecord<?> dbRecord, Field<String> field,
			Class<? extends Enumeration<E>> enumClass) {
		Enumeration<E> enumeration = this.getAppContext().getEnumerationByEnumeration(enumClass);
		EnumProperty<E> property = new EnumPropertyImpl<>(this, dbRecord, field, enumeration);
		this.addProperty(property);
		return property;
	}

	protected <E extends Enumerated> EnumProperty<E> addEnumProperty(String name, Class<E> type) {
		EnumProperty<E> property;
		if (this.getPropertyProvider() == null) {
			property = new EnumPropertyWrapper<>(this, name, type);
		} else {
			property = this.getPropertyProvider().getEnumProperty(this, name, type);
		}
		this.addProperty(property);
		return property;
	}

	protected <E extends Enumerated> EnumSetProperty<E> addEnumSetProperty(String name, Class<E> type) {
		EnumSetProperty<E> property;
		if (this.getPropertyProvider() == null) {
			property = new EnumSetPropertyWrapper<>(this, name, type);
		} else {
			property = this.getPropertyProvider().getEnumSetProperty(this, name, type);
		}
		this.addProperty(property);
		return property;
	}

	protected <A extends Aggregate> ReferenceProperty<A> addReferenceProperty(UpdatableRecord<?> dbRecord,
			Field<Integer> field, Class<A> aggregateClass) {
		ReferenceProperty<A> property = null;
		AggregateCache<A> cache = this.getAppContext().getCache(aggregateClass);
		property = new ReferencePropertyImpl<>(this, dbRecord, field, (id) -> cache.get(id));
		this.addProperty(property);
		return property;
	}

	protected <A extends Aggregate> ReferenceProperty<A> addReferenceProperty(String name, Class<A> type) {
		ReferenceProperty<A> property = null;
		if (this.getPropertyProvider() == null) {
			property = new ReferencePropertyWrapper<>(this, name, type);
		} else {
			property = this.getPropertyProvider().getReferenceProperty(this, name, type);
		}
		this.addProperty(property);
		return property;
	}

	protected <A extends Aggregate> ReferenceSetProperty<A> addReferenceSetProperty(String name, Class<A> type) {
		ReferenceSetProperty<A> property;
		if (this.getPropertyProvider() == null) {
			property = new ReferenceSetPropertyWrapper<>(this, name, type);
		} else {
			property = this.getPropertyProvider().getReferenceSetProperty(this, name, type);
		}
		this.addProperty(property);
		return property;
	}

	protected <P extends Part<?>> PartListProperty<P> addPartListProperty(CodePartListType partListType) {
		requireThis(partListType != null, "partListType not null");
		PartListProperty<P> property = new PartListPropertyImpl<>(this, partListType);
		this.addProperty(property);
		return property;
	}

	protected <P extends Part<?>> PartListProperty<P> addPartListProperty(String name, Class<P> type) {
		PartListProperty<P> property;
		if (this.getPropertyProvider() == null) {
			property = new PartListPropertyWrapper<>(this, name, type);
		} else {
			property = this.getPropertyProvider().getPartListProperty(this, name, type);
		}
		this.addProperty(property);
		return property;
	}

	protected void doBeforeStoreProperties() {
		for (Property<?> p : this.properties) {
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
