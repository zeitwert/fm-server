package io.zeitwert.ddd.persistence.jooq.base;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.lang.reflect.InvocationTargetException;

import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.PartPersistenceProvider;
import io.zeitwert.ddd.part.model.PartPersistenceStatus;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.part.model.base.PartFields;
import io.zeitwert.ddd.part.model.base.PartRepositorySPI;
import io.zeitwert.ddd.part.model.base.PartSPI;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.persistence.jooq.PartState;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesSPI;
import io.zeitwert.ddd.property.model.impl.PropertyFilter;
import io.zeitwert.ddd.property.model.impl.PropertyHandler;
import javassist.util.proxy.ProxyFactory;

public abstract class PartPersistenceProviderBase<A extends Aggregate, P extends Part<A>> extends PropertyProviderBase
		implements PartPersistenceProvider<A, P> {

	static public final String BASE = "base";

	private final DSLContext dslContext;
	private final Class<? extends PartRepository<A, P>> repoIntfClass;

	private final ProxyFactory proxyFactory;
	private final Class<?>[] paramTypeList;

	public PartPersistenceProviderBase(
			final Class<? extends A> aggregateIntfClass,
			Class<? extends PartRepository<A, P>> repoIntfClass,
			Class<? extends Part<A>> baseClass,
			DSLContext dslContext) {
		this.dslContext = dslContext;
		this.repoIntfClass = repoIntfClass;
		this.proxyFactory = new ProxyFactory();
		this.proxyFactory.setSuperclass(baseClass);
		this.proxyFactory.setFilter(PropertyFilter.INSTANCE);
		this.paramTypeList = new Class<?>[] { PartRepository.class, aggregateIntfClass, PartState.class };
		this.mapField("id", BASE, "id", Integer.class);
		this.mapField("parentPartId", BASE, "parent_part_id", Integer.class);
		this.mapField("partListTypeId", BASE, "part_list_type_id", String.class);
		this.mapField("seqNr", BASE, "seq_nr", Integer.class);
	}

	protected final DSLContext getDSLContext() {
		return this.dslContext;
	}

	protected final PartRepository<A, P> getRepository() {
		return AppContext.getInstance().getBean(this.repoIntfClass);
	}

	@Override
	protected UpdatableRecord<?> getDbRecord(EntityWithPropertiesSPI entity, String tableType) {
		return this.getDbRecord(entity);
	}

	protected UpdatableRecord<?> getDbRecord(EntityWithPropertiesSPI entity) {
		Object state = ((PartSPI<?>) entity).getPartState();
		return ((PartState) state).dbRecord();
	}

	@SuppressWarnings("unchecked")
	protected final P newPart(A aggregate, PartState state) {
		P part = null;
		try {
			Object[] params = new Object[] { this.getRepository(), aggregate, state };
			part = (P) this.proxyFactory.create(this.paramTypeList, params, PropertyHandler.INSTANCE);
		} catch (NoSuchMethodException | IllegalArgumentException | InstantiationException | IllegalAccessException
				| InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException("could not create part " + this.getClass().getSimpleName());
		}
		return part;
	}

	@Override
	public PartPersistenceStatus getPersistenceStatus(Part<?> part) {
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

	protected final void doInit(Part<?> part, Integer partId, Part<?> parent, CodePartListType partListType) {
		UpdatableRecord<?> dbRecord = this.getDbRecord((EntityWithPropertiesSPI) part);
		PartRepositorySPI<?, ?> repoSpi = (PartRepositorySPI<?, ?>) part.getMeta().getRepository();
		requireThis(!repoSpi.hasPartId() || partId != null, "valid id");
		if (repoSpi.hasPartId()) {
			dbRecord.setValue(PartFields.ID, partId);
		}
		dbRecord.setValue(PartFields.PARENT_PART_ID, parent != null ? parent.getId() : 0);
		dbRecord.setValue(PartFields.PART_LIST_TYPE_ID, partListType.getId());
	}

	@Override
	public void doStore(P part) {
		UpdatableRecord<?> dbRecord = this.getDbRecord((EntityWithPropertiesSPI) part);
		if (this.getPersistenceStatus(part) == PartPersistenceStatus.DELETED) {
			dbRecord.delete();
		} else if (this.getPersistenceStatus(part) != PartPersistenceStatus.READ) {
			dbRecord.store();
		}
	}

}
