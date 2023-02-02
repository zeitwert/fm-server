package io.zeitwert.ddd.persistence.jooq.base;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.aggregate.model.base.AggregateSPI;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.persistence.AggregatePersistenceProvider;
import io.zeitwert.ddd.persistence.jooq.AggregateState;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;

record FieldConfig(String tableType, String fieldName, Class<?> fieldType) {
}

record CollectionConfig(CodePartListType partListType, Class<?> fieldType) {
}

public abstract class AggregatePersistenceProviderBase<A extends Aggregate> extends PropertyProviderBase
		implements AggregatePersistenceProvider<A> {

	static public final String BASE = "base";
	static public final String EXTN = "extn";

	private final DSLContext dslContext;
	private final Class<? extends AggregateRepository<A, ?>> repoIntfClass;

	public AggregatePersistenceProviderBase(
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

	@Override
	protected UpdatableRecord<?> getDbRecord(EntityWithPropertiesSPI entity, String tableType) {
		Object state = ((AggregateSPI) entity).getAggregateState();
		if (EXTN.equals(tableType)) {
			return ((AggregateState) state).extnRecord();
		} else if (BASE.equals(tableType)) {
			return ((AggregateState) state).baseRecord();
		}
		return null;
	}

}
