package io.dddrive.jooq.ddd;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;

import io.dddrive.ddd.model.Aggregate;
import io.dddrive.ddd.model.Part;
import io.dddrive.ddd.model.PartPersistenceProvider;
import io.dddrive.ddd.model.PartPersistenceStatus;
import io.dddrive.ddd.model.PartRepository;
import io.dddrive.ddd.model.base.PartRepositorySPI;
import io.dddrive.property.model.base.EntityWithPropertiesSPI;

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
