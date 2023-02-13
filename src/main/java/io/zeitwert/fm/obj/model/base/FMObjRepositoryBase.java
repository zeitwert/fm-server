package io.zeitwert.fm.obj.model.base;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.TableRecord;

import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import io.dddrive.app.service.api.AppContext;
import io.dddrive.ddd.model.AggregateRepository;
import io.dddrive.obj.model.Obj;
import io.dddrive.jooq.obj.JooqObjRepositoryBase;
import io.dddrive.jooq.util.SqlUtils;
import io.zeitwert.fm.app.model.RequestContextFM;
import io.zeitwert.fm.ddd.model.base.AggregateFindMixin;

public abstract class FMObjRepositoryBase<O extends Obj, V extends TableRecord<?>>
		extends JooqObjRepositoryBase<O, V>
		implements ObjPersistenceProviderMixin<O>, AggregateFindMixin<V> {

	public FMObjRepositoryBase(
			Class<? extends AggregateRepository<O, V>> repoIntfClass,
			Class<? extends Obj> intfClass,
			Class<? extends Obj> baseClass,
			String aggregateTypeId,
			AppContext appContext,
			DSLContext dslContext) {
		super(repoIntfClass, intfClass, baseClass, aggregateTypeId, appContext, dslContext);
	}

	@Override
	public final AggregateRepository<O, V> getRepository() {
		return this;
	}

	@Override
	public final List<V> find(QuerySpec querySpec) {
		querySpec = this.queryWithFilter(querySpec, (RequestContextFM) this.getAppContext().getRequestContext());
		if (!SqlUtils.hasFilterFor(querySpec, "isClosed")) {
			querySpec.addFilter(PathSpec.of(ObjFields.CLOSED_AT.getName()).filter(FilterOperator.EQ, null));
		}
		return this.doFind(querySpec);
	}

}
