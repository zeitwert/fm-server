package fm.comunas.ddd.property.model.base;

import fm.comunas.ddd.aggregate.model.Aggregate;
import fm.comunas.ddd.aggregate.model.AggregateRepository;
import fm.comunas.ddd.enums.model.Enumerated;
import fm.comunas.ddd.enums.model.Enumeration;
import fm.comunas.ddd.part.model.Part;
import fm.comunas.ddd.property.model.EnumProperty;
import fm.comunas.ddd.property.model.EnumSetProperty;
import fm.comunas.ddd.property.model.EntityPartItem;
import fm.comunas.ddd.property.model.EntityWithProperties;
import fm.comunas.ddd.property.model.PartListProperty;
import fm.comunas.ddd.property.model.Property;
import fm.comunas.ddd.property.model.ReferenceProperty;
import fm.comunas.ddd.property.model.ReferenceSetProperty;
import fm.comunas.ddd.property.model.SimpleProperty;
import fm.comunas.ddd.property.model.enums.CodePartListType;
import fm.comunas.ddd.property.model.impl.EnumPropertyImpl;
import fm.comunas.ddd.property.model.impl.EnumSetPropertyImpl;
import fm.comunas.ddd.property.model.impl.PartListPropertyImpl;
import fm.comunas.ddd.property.model.impl.ReferencePropertyImpl;
import fm.comunas.ddd.property.model.impl.ReferenceSetPropertyImpl;
import fm.comunas.ddd.property.model.impl.SimplePropertyImpl;

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

}
