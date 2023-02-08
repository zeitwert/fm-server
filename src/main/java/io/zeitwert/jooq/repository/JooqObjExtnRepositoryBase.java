package io.zeitwert.jooq.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableRecord;

import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.base.ObjFields;
import io.zeitwert.ddd.obj.model.base.ObjRepositoryBase;
import io.zeitwert.jooq.persistence.ObjPersistenceProviderMixin;
import io.zeitwert.jooq.property.ObjExtnPropertyProviderMixin;
import io.zeitwert.jooq.util.SqlUtils;

public abstract class JooqObjExtnRepositoryBase<O extends Obj, V extends TableRecord<?>>
		extends ObjRepositoryBase<O, V>
		implements ObjExtnPropertyProviderMixin, ObjPersistenceProviderMixin<O>, JooqAggregateFinderMixin<V> {

	private final DSLContext dslContext;
	private final Map<String, Object> dbConfigMap = new HashMap<>();

	public JooqObjExtnRepositoryBase(
			Class<? extends AggregateRepository<O, V>> repoIntfClass,
			Class<? extends Obj> intfClass,
			Class<? extends Obj> baseClass,
			String aggregateTypeId,
			AppContext appContext,
			DSLContext dslContext) {
		super(repoIntfClass, intfClass, baseClass, aggregateTypeId, appContext);
		this.dslContext = dslContext;
		this.mapProperties();
	}

	@Override
	public final Class<? extends Aggregate> getEntityClass() {
		return this.getAggregateClass();
	}

	@Override
	public final DSLContext dslContext() {
		return this.dslContext;
	}

	@Override
	public final Map<String, Object> dbConfigMap() {
		return this.dbConfigMap;
	}

	@Override
	public final AggregateRepository<O, V> getRepository() {
		return this;
	}

	@Override
	public List<V> doFind(Table<? extends Record> table, Field<Integer> idField, QuerySpec querySpec) {
		if (!SqlUtils.hasFilterFor(querySpec, "isClosed")) {
			querySpec.addFilter(PathSpec.of(ObjFields.CLOSED_AT.getName()).filter(FilterOperator.EQ, null));
		}
		return JooqAggregateFinderMixin.super.doFind(table, idField, querySpec);
	}

}
