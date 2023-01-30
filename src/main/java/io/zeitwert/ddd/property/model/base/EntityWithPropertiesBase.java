package io.zeitwert.ddd.property.model.base;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.Field;
import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.aggregate.service.api.AggregateCache;
import io.zeitwert.ddd.enums.model.Enumerated;
import io.zeitwert.ddd.enums.model.Enumeration;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.AggregatePartItem;
import io.zeitwert.ddd.property.model.EntityWithProperties;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.EnumSetProperty;
import io.zeitwert.ddd.property.model.ItemSetProperty;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.ReferenceSetProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.impl.EnumPropertyImpl;
import io.zeitwert.ddd.property.model.impl.EnumSetPropertyImpl;
import io.zeitwert.ddd.property.model.impl.ItemSetPropertyImpl;
import io.zeitwert.ddd.property.model.impl.PartListPropertyImpl;
import io.zeitwert.ddd.property.model.impl.ReferencePropertyImpl;
import io.zeitwert.ddd.property.model.impl.ReferenceSetPropertyImpl;
import io.zeitwert.ddd.property.model.impl.SimplePropertyImpl;

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
	public Map<String, Property<?>> getPropertyMap() {
		return Map.copyOf(this.propertyMap);
	}

	@Override
	public List<Property<?>> getProperties() {
		return List.copyOf(this.properties);
	}

	@Override
	public AggregatePartItem<?> addItem(Property<?> property, CodePartListType partListType) {
		throw new NoSuchMethodError(
				this.getClass().getSimpleName() + ".addItem() [" + property.getName() + ", " + partListType + "]");
	}

	@Override
	public <P extends Part<?>> P addPart(Property<P> property, CodePartListType partListType) {
		throw new NoSuchMethodError(
				this.getClass().getSimpleName() + ".addPart() [" + property.getName() + ", " + partListType + "]");
	}

	protected void addProperty(Property<?> property) {
		requireThis(!this.hasProperty(property.getName()), "property [" + property.getName() + "] is unique");
		this.propertyMap.put(property.getName(), property);
		this.properties.add(property);
	}

	protected <T> SimpleProperty<T> addSimpleProperty(UpdatableRecord<?> dbRecord, Field<T> field) {
		final SimpleProperty<T> property = new SimplePropertyImpl<T>(this, dbRecord, field);
		this.addProperty(property);
		return property;
	}

	protected <E extends Enumerated> EnumProperty<E> addEnumProperty(UpdatableRecord<?> dbRecord, Field<String> field,
			Class<? extends Enumeration<E>> enumClass) {
		final Enumeration<E> enumeration = this.getAppContext().getEnumeration(enumClass);
		final EnumProperty<E> property = new EnumPropertyImpl<E>(this, dbRecord, field, enumeration);
		this.addProperty(property);
		return property;
	}

	protected <T extends Aggregate> ReferenceProperty<T> addReferenceProperty(UpdatableRecord<?> dbRecord,
			Field<Integer> field, Class<T> aggregateClass) {
		ReferenceProperty<T> property = null;
		AggregateCache<T> cache = this.getAppContext().getCache(aggregateClass);
		if (cache != null) {
			property = new ReferencePropertyImpl<T>(this, dbRecord, field, (id) -> cache.get(id));
		} else {
			AggregateRepository<T, ?> repository = this.getAppContext().getRepository(aggregateClass);
			property = new ReferencePropertyImpl<T>(this, dbRecord, field, (id) -> repository.get(id));
		}
		this.addProperty(property);
		return property;
	}

	protected <T extends Aggregate> ReferenceSetProperty<T> addReferenceSetProperty(CodePartListType partListType,
			Class<T> aggregateClass) {
		requireThis(partListType != null, "partListType not null");
		final AggregateRepository<T, ?> repository = this.getAppContext().getRepository(aggregateClass);
		final ReferenceSetProperty<T> property = new ReferenceSetPropertyImpl<T>(this, partListType, repository);
		this.addProperty(property);
		return property;
	}

	protected <E extends Enumerated> EnumSetProperty<E> addEnumSetProperty(CodePartListType partListType,
			Class<? extends Enumeration<E>> enumClass) {
		requireThis(partListType != null, "partListType not null");
		final Enumeration<E> enumeration = this.getAppContext().getEnumeration(enumClass);
		final EnumSetProperty<E> property = new EnumSetPropertyImpl<E>(this, partListType, enumeration);
		this.addProperty(property);
		return property;
	}

	protected <Item extends AggregatePartItem<?>> ItemSetProperty<Item> addItemSetProperty(
			CodePartListType partListType) {
		requireThis(partListType != null, "partListType not null");
		final ItemSetProperty<Item> property = new ItemSetPropertyImpl<Item>(this, partListType);
		this.addProperty(property);
		return property;
	}

	protected <P extends Part<?>> PartListProperty<P> addPartListProperty(CodePartListType partListType) {
		requireThis(partListType != null, "partListType not null");
		final PartListProperty<P> property = new PartListPropertyImpl<P>(this, partListType);
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
