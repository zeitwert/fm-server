package io.zeitwert.ddd.db.model.jooq;

import static io.zeitwert.ddd.util.Check.assertThis;
import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.HashMap;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.UpdatableRecord;
import org.jooq.impl.DSL;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.aggregate.model.base.AggregateSPI;
import io.zeitwert.ddd.aggregate.service.api.AggregateCache;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.db.model.PersistenceProvider;
import io.zeitwert.ddd.enums.model.Enumerated;
import io.zeitwert.ddd.enums.model.Enumeration;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.part.model.enums.CodePartListTypeEnum;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.EnumSetProperty;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.ReferenceSetProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;
import io.zeitwert.ddd.property.model.impl.EnumPropertyImpl;
import io.zeitwert.ddd.property.model.impl.EnumSetPropertyImpl;
import io.zeitwert.ddd.property.model.impl.PartListPropertyImpl;
import io.zeitwert.ddd.property.model.impl.ReferencePropertyImpl;
import io.zeitwert.ddd.property.model.impl.ReferenceSetPropertyImpl;
import io.zeitwert.ddd.property.model.impl.SimplePropertyImpl;

record FieldConfig(String tableType, String fieldName, Class<?> fieldType) {
}

record CollectionConfig(CodePartListType partListType, Class<?> fieldType) {
}

public abstract class PersistenceProviderBase<A extends Aggregate> implements PersistenceProvider<A> {

	static public final String BASE = "base";
	static public final String EXTN = "extn";

	private final DSLContext dslContext;
	private final Class<? extends AggregateRepository<A, ?>> repoIntfClass;
	private final Map<String, Object> dbConfigMap = new HashMap<>();

	public PersistenceProviderBase(
			Class<? extends AggregateRepository<A, ?>> repoIntfClass,
			Class<? extends Aggregate> baseClass,
			DSLContext dslContext) {
		this.repoIntfClass = repoIntfClass;
		this.dslContext = dslContext;
	}

	protected final DSLContext getDSLContext() {
		return this.dslContext;
	}

	protected final AggregateRepository<A, ?> getRepository() {
		return AppContext.getInstance().getBean(this.repoIntfClass);
	}

	protected void mapField(String name, String tableType, String fieldName, Class<?> fieldType) {
		requireThis(this.getFieldConfig(name) == null, "unique field " + name);
		this.dbConfigMap.put(name, new FieldConfig(tableType, fieldName, fieldType));
	}

	protected void mapCollection(String name, String partListTypeName, Class<?> fieldType) {
		requireThis(this.getCollectionConfig(name) == null, "unique collection " + name);
		CodePartListType partListType = CodePartListTypeEnum.getPartListType(partListTypeName);
		this.dbConfigMap.put(name, new CollectionConfig(partListType, fieldType));
	}

	private FieldConfig getFieldConfig(String name) {
		return (FieldConfig) this.dbConfigMap.get(name);
	}

	private CollectionConfig getCollectionConfig(String name) {
		return (CollectionConfig) this.dbConfigMap.get(name);
	}

	private <T> Field<T> checkFieldConfig(FieldConfig fieldConfig, EntityWithPropertiesSPI entity, String name,
			Class<T> type) {
		if (fieldConfig == null) {
			assertThis(false, "field [" + name + "] has valid db configuration");
			return null;
		}
		assertThis(fieldConfig.fieldType() == type, "field [" + name + "] has matching type");
		Field<T> field = DSL.field(fieldConfig.fieldName(), type);
		UpdatableRecord<?> dbRecord = this.getRecord(entity, fieldConfig.tableType());
		assertThis(dbRecord.field(field.getName()) != null, "field [" + name + "] contained in "
				+ (EXTN.equals(fieldConfig.tableType()) ? "extnRecord" : "baseRecord"));
		return field;
	}

	private <T> void checkCollectionConfig(CollectionConfig collectionConfig, EntityWithPropertiesSPI entity, String name,
			Class<T> type) {
		assertThis(collectionConfig != null, "field [" + name + "] has valid db configuration");
	}

	@Override
	public <T> SimpleProperty<T> getSimpleProperty(EntityWithPropertiesSPI entity, String name, Class<T> type) {
		FieldConfig fieldConfig = this.getFieldConfig(name);
		Field<T> field = this.checkFieldConfig(fieldConfig, entity, name, type);
		UpdatableRecord<?> dbRecord = this.getRecord(entity, fieldConfig.tableType());
		return new SimplePropertyImpl<>(entity, dbRecord, name, field);
	}

	@Override
	public <E extends Enumerated> EnumProperty<E> getEnumProperty(EntityWithPropertiesSPI entity, String name,
			Class<E> enumType) {
		FieldConfig fieldConfig = this.getFieldConfig(name);
		Field<String> field = this.checkFieldConfig(fieldConfig, entity, name, String.class);
		Enumeration<E> enumeration = AppContext.getInstance().getEnumeration(enumType);
		UpdatableRecord<?> dbRecord = this.getRecord(entity, fieldConfig.tableType());
		return new EnumPropertyImpl<>(entity, dbRecord, field, enumeration);
	}

	@Override
	public <E extends Enumerated> EnumSetProperty<E> getEnumSetProperty(EntityWithPropertiesSPI entity, String name,
			Class<E> enumType) {
		CollectionConfig collectionConfig = this.getCollectionConfig(name);
		this.checkCollectionConfig(collectionConfig, entity, name, enumType);
		Enumeration<E> enumeration = AppContext.getInstance().getEnumeration(enumType);
		return new EnumSetPropertyImpl<>(entity, name, collectionConfig.partListType(), enumeration);
	}

	@Override
	public <Aggr extends Aggregate> ReferenceProperty<Aggr> getReferenceProperty(EntityWithPropertiesSPI entity,
			String name,
			Class<Aggr> aggregateType) {
		FieldConfig fieldConfig = this.getFieldConfig(name);
		Field<Integer> field = this.checkFieldConfig(fieldConfig, entity, name, Integer.class);
		AggregateCache<Aggr> cache = AppContext.getInstance().getCache(aggregateType);
		UpdatableRecord<?> dbRecord = this.getRecord(entity, fieldConfig.tableType());
		return new ReferencePropertyImpl<>(entity, dbRecord, field, (id) -> cache.get(id));
	}

	@Override
	public <Aggr extends Aggregate> ReferenceSetProperty<Aggr> getReferenceSetProperty(EntityWithPropertiesSPI entity,
			String name, Class<Aggr> aggregateType) {
		CollectionConfig collectionConfig = this.getCollectionConfig(name);
		this.checkCollectionConfig(collectionConfig, entity, name, aggregateType);
		AggregateRepository<Aggr, ?> cache = AppContext.getInstance().getRepository(aggregateType);
		return new ReferenceSetPropertyImpl<>(entity, name, collectionConfig.partListType(), (id) -> cache.get(id));
	}

	@Override
	public <P extends Part<?>> PartListProperty<P> getPartListProperty(EntityWithPropertiesSPI entity, String name,
			Class<P> partType) {
		CollectionConfig collectionConfig = this.getCollectionConfig(name);
		this.checkCollectionConfig(collectionConfig, entity, name, partType);
		return new PartListPropertyImpl<>(entity, name, collectionConfig.partListType());
	}

	private UpdatableRecord<?> getRecord(EntityWithPropertiesSPI entity, String tableType) {
		Object state = ((AggregateSPI) entity).getAggregateState();
		if (EXTN.equals(tableType)) {
			return ((AggregateState) state).getExtnRecord();
		} else if (BASE.equals(tableType)) {
			return ((AggregateState) state).getBaseRecord();
		}
		return null;
	}

}
