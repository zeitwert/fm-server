package io.dddrive.jooq.ddd;

import static io.dddrive.util.Invariant.requireThis;

import java.util.HashMap;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;
import org.springframework.beans.factory.annotation.Autowired;

import io.dddrive.ddd.model.Aggregate;
import io.dddrive.ddd.model.Part;
import io.dddrive.ddd.model.PartRepository;
import io.dddrive.ddd.model.base.PartRepositoryBase;
import io.dddrive.ddd.model.base.PartRepositorySPI;
import io.dddrive.ddd.model.base.PartSPI;
import io.dddrive.ddd.model.enums.CodePartListType;
import io.dddrive.property.model.base.EntityWithPropertiesSPI;

public abstract class JooqPartRepositoryBase<A extends Aggregate, P extends Part<A>> extends PartRepositoryBase<A, P>
		implements PartPersistenceProviderMixin<A, P> {

	private DSLContext dslContext;
	private final Map<String, Object> dbConfigMap = new HashMap<>();

	protected JooqPartRepositoryBase(
			Class<? extends A> aggregateIntfClass,
			Class<? extends Part<A>> intfClass,
			Class<? extends Part<A>> baseClass,
			String partTypeId) {
		super(aggregateIntfClass, intfClass, baseClass, partTypeId);
	}

	@Autowired
	protected void setDSLContext(DSLContext dslContext) {
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
	public final PartRepository<A, P> getRepository() {
		return this; // downcall from providers
	}

	protected final PartRepositorySPI<A, P> getRepositorySPI() {
		return this; // needed from implementations
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
		requireThis(!this.hasPartId() || partId != null, "valid id");
		if (this.hasPartId()) {
			dbRecord.setValue(PartFields.ID, partId);
		}
		dbRecord.setValue(PartFields.PARENT_PART_ID, parent != null ? parent.getId() : 0);
		dbRecord.setValue(PartFields.PART_LIST_TYPE_ID, partListType.getId());
	}

}
