package io.zeitwert.ddd.persistence.jooq.base;

import java.util.HashMap;
import java.util.Map;

import org.jooq.DSLContext;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.app.service.api.AppContext;

public abstract class AggregatePersistenceProviderBase<A extends Aggregate> {

	private final Map<String, Object> dbConfigMap = new HashMap<>();
	private final DSLContext dslContext;
	private final Class<? extends AggregateRepository<A, ?>> repoIntfClass;

	public AggregatePersistenceProviderBase(
			Class<? extends AggregateRepository<A, ?>> repoIntfClass,
			Class<? extends Aggregate> baseClass,
			DSLContext dslContext) {
		this.repoIntfClass = repoIntfClass;
		this.dslContext = dslContext;
	}

	public final Map<String, Object> dbConfigMap() {
		return this.dbConfigMap;
	}

	public final DSLContext dslContext() {
		return this.dslContext;
	}

	public final AggregateRepository<A, ?> getRepository() {
		return AppContext.getInstance().getBean(this.repoIntfClass);
	}

}
