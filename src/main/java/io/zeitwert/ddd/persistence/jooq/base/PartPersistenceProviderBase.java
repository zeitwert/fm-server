package io.zeitwert.ddd.persistence.jooq.base;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.HashMap;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.PartPersistenceProvider;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.part.model.base.PartFields;
import io.zeitwert.ddd.part.model.base.PartRepositorySPI;
import io.zeitwert.ddd.part.model.base.PartSPI;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.persistence.jooq.PartState;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;

public abstract class PartPersistenceProviderBase<A extends Aggregate, P extends Part<A>>
		implements PartPersistenceProvider<A, P> {

	private final Class<? extends Part<A>> intfClass;
	private final DSLContext dslContext;
	private final Map<String, Object> dbConfigMap = new HashMap<>();

	public PartPersistenceProviderBase(Class<? extends Part<A>> intfClass, DSLContext dslContext) {
		this.intfClass = intfClass;
		this.dslContext = dslContext;
	}

	@Override
	public final Class<? extends Part<A>> getEntityClass() {
		return this.intfClass;
	}

	public Map<String, Object> dbConfigMap() {
		return this.dbConfigMap;
	}

	public final DSLContext dslContext() {
		return this.dslContext;
	}

	@SuppressWarnings("unchecked")
	public final PartRepository<A, P> getRepository() {
		return (PartRepository<A, P>) AppContext.getInstance().getPartRepository(this.intfClass);
	}

	@SuppressWarnings("unchecked")
	protected final PartRepositorySPI<A, P> getRepositorySPI() {
		return (PartRepositorySPI<A, P>) this.getRepository();
	}

	public UpdatableRecord<?> getDbRecord(EntityWithPropertiesSPI entity, String tableType) {
		return this.getDbRecord(entity);
	}

	public UpdatableRecord<?> getDbRecord(EntityWithPropertiesSPI entity) {
		Object state = ((PartSPI<?>) entity).getPartState();
		return ((PartState) state).dbRecord();
	}

	protected void doInit(Part<?> part, Integer partId, Part<?> parent, CodePartListType partListType) {
		UpdatableRecord<?> dbRecord = this.getDbRecord((EntityWithPropertiesSPI) part);
		PartRepositorySPI<?, ?> repoSpi = (PartRepositorySPI<?, ?>) part.getMeta().getRepository();
		requireThis(!repoSpi.hasPartId() || partId != null, "valid id");
		if (repoSpi.hasPartId()) {
			dbRecord.setValue(PartFields.ID, partId);
		}
		dbRecord.setValue(PartFields.PARENT_PART_ID, parent != null ? parent.getId() : 0);
		dbRecord.setValue(PartFields.PART_LIST_TYPE_ID, partListType.getId());
	}

}
