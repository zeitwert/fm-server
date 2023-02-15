package io.zeitwert.fm.obj.model.base;

import java.util.List;

import org.jooq.TableRecord;

import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import io.dddrive.ddd.model.AggregateRepository;
import io.dddrive.ddd.model.base.AggregateRepositorySPI;
import io.dddrive.obj.model.Obj;
import io.dddrive.jooq.obj.JooqObjExtnRepositoryBase;
import io.dddrive.jooq.util.SqlUtils;
import io.zeitwert.fm.app.model.RequestContextFM;
import io.zeitwert.fm.ddd.model.base.AggregateFindMixin;

public abstract class FMObjExtnRepositoryBase<O extends Obj, V extends TableRecord<?>>
		extends JooqObjExtnRepositoryBase<O, V>
		implements ObjPersistenceProviderMixin<O>, AggregateFindMixin<V> {

	public FMObjExtnRepositoryBase(
			Class<? extends AggregateRepository<O, V>> repoIntfClass,
			Class<? extends Obj> intfClass,
			Class<? extends Obj> baseClass,
			String aggregateTypeId) {
		super(repoIntfClass, intfClass, baseClass, aggregateTypeId);
	}

	@Override
	public final AggregateRepositorySPI<O, V> repositorySPI() {
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
