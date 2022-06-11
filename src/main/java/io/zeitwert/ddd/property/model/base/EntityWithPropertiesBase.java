package io.zeitwert.ddd.property.model.base;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.enums.model.Enumerated;
import io.zeitwert.ddd.enums.model.Enumeration;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.EnumSetProperty;
import io.zeitwert.ddd.property.model.EntityPartItem;
import io.zeitwert.ddd.property.model.EntityWithProperties;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.ReferenceSetProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.impl.EnumPropertyImpl;
import io.zeitwert.ddd.property.model.impl.EnumSetPropertyImpl;
import io.zeitwert.ddd.property.model.impl.PartListPropertyImpl;
import io.zeitwert.ddd.property.model.impl.ReferencePropertyImpl;
import io.zeitwert.ddd.property.model.impl.ReferenceSetPropertyImpl;
import io.zeitwert.ddd.property.model.impl.SimplePropertyImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.Field;
import org.jooq.UpdatableRecord;
import org.springframework.util.Assert;

public abstract class EntityWithPropertiesBase implements EntityWithProperties, EntityWithPropertiesSPI {

	private Map<String, Property<?>> propertyMap = new HashMap<>();
	private List<Property<?>> propertyList = new ArrayList<>();

	protected void require(boolean condition, String message) {
		Assert.isTrue(condition, "Precondition failed: " + message);
	}

	@Override
	public boolean hasProperty(String name) {
		return this.propertyMap.containsKey(name);
	}

	@Override
	public Property<?> getProperty(String name) {
		return this.propertyMap.get(name);
	}

	@Override
	public Map<String, Property<?>> getPropertyMap() {
		return Map.copyOf(this.propertyMap);
	}

	@Override
	public List<Property<?>> getPropertyList() {
		return List.copyOf(this.propertyList);
	}

	@Override
	public EntityPartItem addItem(Property<?> property, CodePartListType partListType) {
		throw new NoSuchMethodError(
				this.getClass().getSimpleName() + ".addItem() [" + property.getName() + ", " + partListType + "]");
	}

	@Override
	public <P extends Part<?>> P addPart(Property<P> property, CodePartListType partListType) {
		throw new NoSuchMethodError(
				this.getClass().getSimpleName() + ".addPart() [" + property.getName() + ", " + partListType + "]");
	}

	protected void addProperty(Property<?> property) {
		require(!this.hasProperty(property.getName()), "property [" + property.getName() + "] is unique");
		this.propertyMap.put(property.getName(), property);
		this.propertyList.add(property);
	}

	protected <T> SimpleProperty<T> addSimpleProperty(UpdatableRecord<?> dbRecord, Field<T> field) {
		final SimpleProperty<T> property = new SimplePropertyImpl<T>(this, dbRecord, field);
		this.addProperty(property);
		return property;
	}

	protected <E extends Enumerated> EnumProperty<E> addEnumProperty(UpdatableRecord<?> dbRecord, Field<String> field,
			Class<? extends Enumeration<E>> enumClass) {
		final Enumeration<E> enumeration = this.getMeta().getAppContext().getEnumeration(enumClass);
		final EnumProperty<E> property = new EnumPropertyImpl<E>(this, dbRecord, field, enumeration);
		this.addProperty(property);
		return property;
	}

	protected <T extends Aggregate> ReferenceProperty<T> addReferenceProperty(UpdatableRecord<?> dbRecord,
			Field<Integer> field, Class<T> aggregateClass) {
		final AggregateRepository<T, ?> repository = this.getMeta().getAppContext().getRepository(aggregateClass);
		final ReferenceProperty<T> property = new ReferencePropertyImpl<T>(this, dbRecord, field, repository);
		this.addProperty(property);
		return property;
	}

	protected <E extends Enumerated> EnumSetProperty<E> addEnumSetProperty(CodePartListType partListType,
			Class<? extends Enumeration<E>> enumClass) {
		require(partListType != null, "partListType not null");
		final Enumeration<E> enumeration = this.getMeta().getAppContext().getEnumeration(enumClass);
		final EnumSetProperty<E> property = new EnumSetPropertyImpl<E>(this, partListType, enumeration);
		this.addProperty(property);
		return property;
	}

	protected <T extends Aggregate> ReferenceSetProperty<T> addReferenceSetProperty(CodePartListType partListType,
			Class<T> aggregateClass) {
		require(partListType != null, "partListType not null");
		final AggregateRepository<T, ?> repository = this.getMeta().getAppContext().getRepository(aggregateClass);
		final ReferenceSetProperty<T> property = new ReferenceSetPropertyImpl<T>(this, partListType, repository);
		this.addProperty(property);
		return property;
	}

	protected <P extends Part<?>> PartListProperty<P> addPartListProperty(CodePartListType partListType) {
		require(partListType != null, "partListType not null");
		final PartListProperty<P> property = new PartListPropertyImpl<P>(this, partListType);
		this.addProperty(property);
		return property;
	}

	protected void doBeforeStoreProperties() {
		for (Property<?> p : this.propertyList) {
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
