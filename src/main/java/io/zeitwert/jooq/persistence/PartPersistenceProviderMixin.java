package io.zeitwert.jooq.persistence;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.PartPersistenceProvider;
import io.zeitwert.ddd.part.model.PartPersistenceStatus;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.part.model.base.PartRepositorySPI;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;
import io.zeitwert.jooq.property.PartFields;

public interface PartPersistenceProviderMixin<A extends Aggregate, P extends Part<A>>
		extends PartPersistenceProvider<A, P> {

	DSLContext dslContext();

	PartRepository<A, P> getRepository();

	UpdatableRecord<?> getDbRecord(EntityWithPropertiesSPI entity);

	@Override
	default PartPersistenceStatus getPersistenceStatus(Part<?> part) {
		PartRepositorySPI<?, ?> repoSpi = (PartRepositorySPI<?, ?>) this.getRepository();
		UpdatableRecord<?> dbRecord = this.getDbRecord((EntityWithPropertiesSPI) part);
		if (part.getMeta().isDeleted()) {
			return PartPersistenceStatus.DELETED;
		} else if (repoSpi.hasPartId() && dbRecord.changed(PartFields.ID)) {
			return PartPersistenceStatus.CREATED;
		} else if (dbRecord.changed()) {
			return PartPersistenceStatus.UPDATED;
		} else {
			return PartPersistenceStatus.READ;
		}
	}

	@Override
	default void doStore(P part) {
		UpdatableRecord<?> dbRecord = this.getDbRecord((EntityWithPropertiesSPI) part);
		if (this.getPersistenceStatus(part) == PartPersistenceStatus.DELETED) {
			dbRecord.delete();
		} else if (this.getPersistenceStatus(part) != PartPersistenceStatus.READ) {
			dbRecord.store();
		}
	}

}
