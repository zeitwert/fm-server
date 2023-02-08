package io.zeitwert.jooq.repository;

import java.util.HashMap;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.TableRecord;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.base.ObjRepositoryBase;
import io.zeitwert.jooq.persistence.ObjPersistenceProviderMixin;
import io.zeitwert.jooq.property.ObjPropertyProviderMixin;

public abstract class JooqObjRepositoryBase<O extends Obj, V extends TableRecord<?>>
		extends ObjRepositoryBase<O, V>
		implements ObjPropertyProviderMixin, ObjPersistenceProviderMixin<O> {

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

}
