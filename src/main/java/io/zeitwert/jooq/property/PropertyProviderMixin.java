package io.zeitwert.jooq.property;

import static io.zeitwert.ddd.util.Check.assertThis;
import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.Map;

import org.jooq.Field;
import org.jooq.UpdatableRecord;
import org.jooq.impl.DSL;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.aggregate.service.api.AggregateCache;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.enums.model.Enumerated;
import io.zeitwert.ddd.enums.model.Enumeration;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.part.model.enums.CodePartListTypeEnum;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.EnumSetProperty;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.PropertyProvider;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.ReferenceSetProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;
import io.zeitwert.ddd.property.model.impl.EnumSetPropertyImpl;
import io.zeitwert.ddd.property.model.impl.PartListPropertyImpl;
import io.zeitwert.ddd.property.model.impl.ReferenceSetPropertyImpl;

record FieldConfig(String tableType, String fieldName, Class<?> fieldType) {
}

record CollectionConfig(CodePartListType partListType, Class<?> fieldType) {
}

public interface PropertyProviderMixin extends PropertyProvider {

	Map<String, Object> dbConfigMap();

	void mapFields();

	UpdatableRecord<?> getDbRecord(EntityWithPropertiesSPI entity, String tableType);

	default void mapField(String name, String tableType, String fieldName, Class<?> fieldType) {
		requireThis(this.getFieldConfig(name) == null, "unique field " + name);
		this.dbConfigMap().put(name, new FieldConfig(tableType, fieldName, fieldType));
	}

	default void mapCollection(String name, String partListTypeName, Class<?> fieldType) {
		requireThis(this.getCollectionConfig(name) == null, "unique collection " + name);
		CodePartListType partListType = CodePartListTypeEnum.getPartListType(partListTypeName);
		this.dbConfigMap().put(name, new CollectionConfig(partListType, fieldType));
	}

	@Override
	default <T> SimpleProperty<T> getSimpleProperty(EntityWithPropertiesSPI entity, String name, Class<T> type) {
		FieldConfig fieldConfig = this.getFieldConfig(name);
		this.checkFieldConfig(fieldConfig, entity, name, type);
		Field<T> field = DSL.field(fieldConfig.fieldName(), type);
		UpdatableRecord<?> dbRecord = this.getDbRecord(entity, fieldConfig.tableType());
		return new SimplePropertyImpl<>(entity, dbRecord, name, field);
	}

	@Override
	default <E extends Enumerated> EnumProperty<E> getEnumProperty(EntityWithPropertiesSPI entity, String name,
			Class<E> enumType) {
		FieldConfig fieldConfig = this.getFieldConfig(name);
		this.checkFieldConfig(fieldConfig, entity, name, String.class);
		Field<String> field = DSL.field(fieldConfig.fieldName(), String.class);
		Enumeration<E> enumeration = AppContext.getInstance().getEnumeration(enumType);
		UpdatableRecord<?> dbRecord = this.getDbRecord(entity, fieldConfig.tableType());
		return new EnumPropertyImpl<>(entity, dbRecord, name, field, enumeration);
	}

	@Override
	default <E extends Enumerated> EnumSetProperty<E> getEnumSetProperty(EntityWithPropertiesSPI entity, String name,
			Class<E> enumType) {
		CollectionConfig collectionConfig = this.getCollectionConfig(name);
		this.checkCollectionConfig(collectionConfig, entity, name, enumType);
		Enumeration<E> enumeration = AppContext.getInstance().getEnumeration(enumType);
		return new EnumSetPropertyImpl<>(entity, name, collectionConfig.partListType(), enumeration);
	}

	@Override
	default <Aggr extends Aggregate> ReferenceProperty<Aggr> getReferenceProperty(EntityWithPropertiesSPI entity,
			String name,
			Class<Aggr> aggregateType) {
		FieldConfig fieldConfig = this.getFieldConfig(name);
		this.checkFieldConfig(fieldConfig, entity, name, Integer.class);
		Field<Integer> field = DSL.field(fieldConfig.fieldName(), Integer.class);
		AggregateCache<Aggr> cache = AppContext.getInstance().getCache(aggregateType);
		UpdatableRecord<?> dbRecord = this.getDbRecord(entity, fieldConfig.tableType());
		return new ReferencePropertyImpl<>(entity, dbRecord, name, field, (id) -> cache.get(id));
	}

	@Override
	default <Aggr extends Aggregate> ReferenceSetProperty<Aggr> getReferenceSetProperty(EntityWithPropertiesSPI entity,
			String name, Class<Aggr> aggregateType) {
		CollectionConfig collectionConfig = this.getCollectionConfig(name);
		this.checkCollectionConfig(collectionConfig, entity, name, aggregateType);
		AggregateRepository<Aggr, ?> cache = AppContext.getInstance().getRepository(aggregateType);
		return new ReferenceSetPropertyImpl<>(entity, name, collectionConfig.partListType(), (id) -> cache.get(id));
	}

	@Override
	default <P extends Part<?>> PartListProperty<P> getPartListProperty(EntityWithPropertiesSPI entity, String name,
			Class<P> partType) {
		CollectionConfig collectionConfig = this.getCollectionConfig(name);
		this.checkCollectionConfig(collectionConfig, entity, name, partType);
		return new PartListPropertyImpl<>(entity, name, collectionConfig.partListType());
	}

	private FieldConfig getFieldConfig(String name) {
		return (FieldConfig) this.dbConfigMap().get(name);
	}

	private void checkFieldConfig(FieldConfig fieldConfig, EntityWithPropertiesSPI entity, String name, Class<?> type) {
		if (fieldConfig == null) {
			assertThis(false, "field [" + name + "] has valid db configuration");
		} else {
			assertThis(fieldConfig.fieldType() == type, "field [" + name + "] has matching type");
			UpdatableRecord<?> dbRecord = this.getDbRecord(entity, fieldConfig.tableType());
			assertThis(dbRecord.field(fieldConfig.fieldName()) != null,
					"field [" + name + "/" + fieldConfig.fieldName() + "] contained in database record");
		}
	}

	private CollectionConfig getCollectionConfig(String name) {
		return (CollectionConfig) this.dbConfigMap().get(name);
	}

	private void checkCollectionConfig(CollectionConfig collectionConfig, EntityWithPropertiesSPI entity, String name,
			Class<?> type) {
		assertThis(collectionConfig != null, "field [" + name + "] has valid db configuration");
	}

}
