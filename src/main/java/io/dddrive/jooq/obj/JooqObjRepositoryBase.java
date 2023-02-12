package io.dddrive.jooq.obj;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.TableRecord;

import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import io.dddrive.app.service.api.AppContext;
import io.dddrive.ddd.model.Aggregate;
import io.dddrive.ddd.model.AggregateRepository;
import io.dddrive.jooq.ddd.JooqAggregateFinderMixin;
import io.dddrive.jooq.util.SqlUtils;
import io.dddrive.obj.model.Obj;
import io.dddrive.obj.model.base.ObjRepositoryBase;

public abstract class JooqObjRepositoryBase<O extends Obj, V extends TableRecord<?>>
		extends ObjRepositoryBase<O, V>
		implements ObjPropertyProviderMixin, ObjPersistenceProviderMixin<O>, JooqAggregateFinderMixin<V> {

	private final DSLContext dslContext;
	private final Map<String, Object> dbConfigMap = new HashMap<>();

	public JooqObjRepositoryBase(
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
	public final List<V> find(QuerySpec querySpec) {
		querySpec = this.queryWithFilter(querySpec, this.getAppContext().getRequestContext());
		if (!SqlUtils.hasFilterFor(querySpec, "isClosed")) {
			querySpec.addFilter(PathSpec.of(ObjFields.CLOSED_AT.getName()).filter(FilterOperator.EQ, null));
		}
		return this.doFind(querySpec);
	}

}
