package io.zeitwert.ddd.aggregate.model.base;

import static io.zeitwert.ddd.util.Check.assertThis;

import java.util.HashMap;
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
import io.zeitwert.ddd.obj.model.base.ObjBase;
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
import io.zeitwert.ddd.property.model.impl.EnumPropertyImpl;
import io.zeitwert.ddd.property.model.impl.EnumSetPropertyImpl;
import io.zeitwert.ddd.property.model.impl.PartListPropertyImpl;
import io.zeitwert.ddd.property.model.impl.ReferencePropertyImpl;
import io.zeitwert.ddd.property.model.impl.ReferenceSetPropertyImpl;
import io.zeitwert.ddd.property.model.impl.SimplePropertyImpl;

public abstract class JooqPropertyProviderBase implements PropertyProvider {

	public static enum DbTableType {
		BASE, EXTN
	}

	public static interface DbConfig {

		DbTableType dbTableType();

		String fieldName();

		Class<?> fieldType();

	}

	public static record FieldConfig(DbTableType dbTableType, String fieldName, Class<?> fieldType) implements DbConfig {
	}

	public static record CollectionConfig(CodePartListType partListType, Class<?> fieldType)
			implements DbConfig {
		@Override
		public DbTableType dbTableType() {
			return null;
		}

		@Override
		public String fieldName() {
			return null;
		}
	}

	private final Map<String, DbConfig> dbConfigMap = new HashMap<>();

	private FieldConfig getFieldConfig(String name) {
		return (FieldConfig) this.dbConfigMap.get(name);
	}

	private CollectionConfig getCollectionConfig(String name) {
		return (CollectionConfig) this.dbConfigMap.get(name);
	}

	protected void mapField(String name, DbTableType dbTableType, String fieldName, Class<?> fieldType) {
		this.dbConfigMap.put(name, new FieldConfig(dbTableType, fieldName, fieldType));
	}

	protected void mapCollection(String name, String partListTypeName, Class<?> fieldType) {
		CodePartListType partListType = CodePartListTypeEnum.getPartListType(partListTypeName);
		this.dbConfigMap.put(name, new CollectionConfig(partListType, fieldType));
	}

	private <T> Field<T> checkFieldConfig(FieldConfig fieldConfig, EntityWithPropertiesSPI entity, String name,
			Class<T> type) {
		if (fieldConfig == null) {
			assertThis(false, "field [" + name + "] has valid db configuration");
			return null;
		}
		assertThis(fieldConfig.fieldType() == type, "field [" + name + "] has matching type");
		UpdatableRecord<?> baseRecord = ((ObjBase) entity).baseDbRecord();
		UpdatableRecord<?> extnRecord = ((ObjBase) entity).extnDbRecord();
		Field<T> field = DSL.field(fieldConfig.fieldName(), type);
		if (fieldConfig.dbTableType() == DbTableType.EXTN) {
			assertThis(extnRecord.field(field.getName()) != null, "field [" + name + "] contained in extnRecord");
		} else if (fieldConfig.dbTableType() == DbTableType.BASE) {
			assertThis(baseRecord.field(field.getName()) != null, "field [" + name + "] contained in baseRecord");
		} else {
			assertThis(false, "field [" + name + "] has valid dbTableType");
		}
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
		if (fieldConfig.dbTableType() == DbTableType.EXTN) {
			return new SimplePropertyImpl<>(entity, ((ObjBase) entity).extnDbRecord(), name, field);
		} else if (fieldConfig.dbTableType() == DbTableType.BASE) {
			return new SimplePropertyImpl<>(entity, ((ObjBase) entity).baseDbRecord(), name, field);
		}
		return null;
	}

	@Override
	public <E extends Enumerated> EnumProperty<E> getEnumProperty(EntityWithPropertiesSPI entity, String name,
			Class<E> enumType) {
		FieldConfig fieldConfig = this.getFieldConfig(name);
		Field<String> field = this.checkFieldConfig(fieldConfig, entity, name, String.class);
		Enumeration<E> enumeration = AppContext.getInstance().getEnumeration(enumType);
		if (fieldConfig.dbTableType() == DbTableType.EXTN) {
			return new EnumPropertyImpl<>(entity, ((ObjBase) entity).extnDbRecord(), field, enumeration);
		} else if (fieldConfig.dbTableType() == DbTableType.BASE) {
			return new EnumPropertyImpl<>(entity, ((ObjBase) entity).baseDbRecord(), field, enumeration);
		}
		return null;
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
	public <A extends Aggregate> ReferenceProperty<A> getReferenceProperty(EntityWithPropertiesSPI entity, String name,
			Class<A> aggregateType) {
		FieldConfig fieldConfig = this.getFieldConfig(name);
		Field<Integer> field = this.checkFieldConfig(fieldConfig, entity, name, Integer.class);
		AggregateCache<A> cache = AppContext.getInstance().getCache(aggregateType);
		if (fieldConfig.dbTableType() == DbTableType.EXTN) {
			return new ReferencePropertyImpl<>(entity, ((ObjBase) entity).extnDbRecord(), field, (id) -> cache.get(id));
		} else if (fieldConfig.dbTableType() == DbTableType.BASE) {
			return new ReferencePropertyImpl<>(entity, ((ObjBase) entity).baseDbRecord(), field, (id) -> cache.get(id));
		}
		return null;
	}

	@Override
	public <A extends Aggregate> ReferenceSetProperty<A> getReferenceSetProperty(EntityWithPropertiesSPI entity,
			String name, Class<A> aggregateType) {
		CollectionConfig collectionConfig = this.getCollectionConfig(name);
		this.checkCollectionConfig(collectionConfig, entity, name, aggregateType);
		AggregateRepository<A, ?> cache = AppContext.getInstance().getRepository(aggregateType);
		return new ReferenceSetPropertyImpl<>(entity, name, collectionConfig.partListType(), (id) -> cache.get(id));
	}

	@Override
	public <P extends Part<?>> PartListProperty<P> getPartListProperty(EntityWithPropertiesSPI entity, String name,
			Class<P> partType) {
		CollectionConfig collectionConfig = this.getCollectionConfig(name);
		this.checkCollectionConfig(collectionConfig, entity, name, partType);
		return new PartListPropertyImpl<>(entity, name, collectionConfig.partListType());
	}

}
