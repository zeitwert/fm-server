package io.zeitwert.jooq.repository;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.HashMap;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.part.model.base.PartFields;
import io.zeitwert.ddd.part.model.base.PartRepositoryBase;
import io.zeitwert.ddd.part.model.base.PartRepositorySPI;
import io.zeitwert.ddd.part.model.base.PartSPI;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;
import io.zeitwert.jooq.persistence.PartPersistenceProviderMixin;
import io.zeitwert.jooq.persistence.PartState;

public abstract class JooqPartRepositoryBase<A extends Aggregate, P extends Part<A>> extends PartRepositoryBase<A, P>
		implements PartPersistenceProviderMixin<A, P> {

	private final DSLContext dslContext;
	private final Map<String, Object> dbConfigMap = new HashMap<>();

	protected JooqPartRepositoryBase(
			Class<? extends A> aggregateIntfClass,
			Class<? extends Part<A>> intfClass,
			Class<? extends Part<A>> baseClass,
			String partTypeId,
			AppContext appContext,
			DSLContext dslContext) {
		super(aggregateIntfClass, intfClass, baseClass, partTypeId, appContext);
		this.dslContext = dslContext;
	}

	@Override
	public final Class<? extends Part<A>> getEntityClass() {
		return (Class<? extends Part<A>>) this.getPartClass();
	}

	public final Map<String, Object> dbConfigMap() {
		return this.dbConfigMap;
	}

	@Override
	public final DSLContext dslContext() {
		return this.dslContext;
	}

	@Override
	@SuppressWarnings("unchecked")
	public final PartRepository<A, P> getRepository() {
		return (PartRepository<A, P>) AppContext.getInstance().getPartRepository(this.getEntityClass());
	}

	@SuppressWarnings("unchecked")
	protected final PartRepositorySPI<A, P> getRepositorySPI() {
		return (PartRepositorySPI<A, P>) this.getRepository();
	}

	public final UpdatableRecord<?> getDbRecord(EntityWithPropertiesSPI entity, String tableType) {
		return this.getDbRecord(entity);
	}

	@Override
	public final UpdatableRecord<?> getDbRecord(EntityWithPropertiesSPI entity) {
		Object state = ((PartSPI<?>) entity).getPartState();
		return ((PartState) state).dbRecord();
	}

	protected final void doInit(Part<?> part, Integer partId, Part<?> parent, CodePartListType partListType) {
		UpdatableRecord<?> dbRecord = this.getDbRecord((EntityWithPropertiesSPI) part);
		// PartRepositorySPI<?, ?> repoSpi = (PartRepositorySPI<?, ?>)
		// part.getMeta().getRepository();
		PartRepositorySPI<A, P> repoSpi = this.getRepositorySPI();
		requireThis(!repoSpi.hasPartId() || partId != null, "valid id");
		if (repoSpi.hasPartId()) {
			dbRecord.setValue(PartFields.ID, partId);
		}
		dbRecord.setValue(PartFields.PARENT_PART_ID, parent != null ? parent.getId() : 0);
		dbRecord.setValue(PartFields.PART_LIST_TYPE_ID, partListType.getId());
	}

}
