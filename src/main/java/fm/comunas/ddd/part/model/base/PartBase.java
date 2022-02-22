package fm.comunas.ddd.part.model.base;

import fm.comunas.ddd.aggregate.model.Aggregate;
import fm.comunas.ddd.aggregate.model.base.AggregateSPI;
import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.enums.model.Enumerated;
import fm.comunas.ddd.enums.model.Enumeration;
import fm.comunas.ddd.part.model.Part;
import fm.comunas.ddd.part.model.PartMeta;
import fm.comunas.ddd.property.model.Property;
import fm.comunas.ddd.property.model.SimpleProperty;
import fm.comunas.ddd.property.model.base.EntityWithPropertiesBase;
import fm.comunas.ddd.property.model.enums.CodePartListType;
import fm.comunas.ddd.session.model.SessionInfo;

import java.util.Objects;

import org.jooq.UpdatableRecord;

public abstract class PartBase<A extends Aggregate> extends EntityWithPropertiesBase
		implements Part<A>, PartMeta<A>, PartSPI<A> {

	private A aggregate;
	private final UpdatableRecord<?> dbRecord;

	protected final SimpleProperty<Integer> id;
	protected final SimpleProperty<Integer> parentPartId;
	protected final SimpleProperty<String> partListTypeId;
	protected final SimpleProperty<Integer> seqNr;

	private boolean isDeleted = false;
	private boolean isInCalc = false;

	protected PartBase(A aggregate, UpdatableRecord<?> dbRecord) {
		this.aggregate = aggregate;
		this.dbRecord = dbRecord;
		this.id = this.addSimpleProperty(dbRecord, PartFields.ID);
		this.parentPartId = this.addSimpleProperty(dbRecord, PartFields.PARENT_PART_ID);
		this.partListTypeId = this.addSimpleProperty(dbRecord, PartFields.PART_LIST_TYPE_ID);
		this.seqNr = this.addSimpleProperty(dbRecord, PartFields.SEQ_NR);
	}

	public PartMeta<A> getMeta() {
		return this;
	}

	@Override
	public AppContext getAppContext() {
		return this.getAggregate().getMeta().getAppContext();
	}

	@Override
	public SessionInfo getSessionInfo() {
		return this.getAggregate().getMeta().getSessionInfo();
	}

	protected <E extends Enumeration<?>> boolean isValidEnum(Enumerated item, Class<E> enumClass) {
		Enumeration<?> anEnum = this.getAppContext().getEnumeration(enumClass);
		if (item == null) {
			return true;
		} else if (!Objects.equals(item.getEnumeration(), anEnum)) {
			return false;
		}
		return Objects.equals(item, anEnum.getItem(item.getId()));
	}

	protected boolean isValidAggregateId(Integer id, Class<? extends Aggregate> aggregateClass) {
		return id == null || true; // repo.get(id).isPresent(); <- too expensive
	}

	protected UpdatableRecord<?> getDbRecord() {
		return this.dbRecord;
	}

	@Override
	public Integer getId() {
		return this.id.getValue();
	}

	@Override
	public A getAggregate() {
		return this.aggregate;
	}

	@Override
	public Integer getParentPartId() {
		try {
			return this.parentPartId.getValue();
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	@Override
	public String getPartListTypeId() {
		try {
			return this.partListTypeId.getValue();
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	@Override
	public Integer getSeqNr() {
		try {
			return this.seqNr.getValue();
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	@Override
	public void setSeqNr(Integer seqNr) {
		try {
			this.seqNr.setValue(seqNr);
		} catch (IllegalArgumentException e) {
		}
	}

	@Override
	public abstract void doInit(Integer partId, A aggregate, Part<?> parent, CodePartListType partListType);

	@Override
	public void afterCreate() {
	}

	@Override
	public PartStatus getStatus() {
		if (this.isDeleted) {
			return PartStatus.DELETED;
			// } else if (this.getId() == null || this.getDbRecord().changed(PartFields.ID))
			// {
			// return PartStatus.CREATED;
		} else if (this.getDbRecord().changed()) {
			return PartStatus.UPDATED;
		} else {
			return PartStatus.READ;
		}
	}

	@Override
	public void delete() {
		this.isDeleted = true;
	}

	@Override
	public void store() {
		if (this.getStatus() == PartStatus.DELETED) {
			this.getDbRecord().delete();
		} else if (this.getStatus() != PartStatus.READ) {
			this.getDbRecord().store();
		}
	}

	@Override
	public void afterSet(Property<?> property) {
		this.calcAll();
	}

	@Override
	public void afterAdd(Property<?> property) {
		this.calcAll();
	}

	@Override
	public void afterRemove(Property<?> property) {
		this.calcAll();
	}

	protected Boolean isInCalc() {
		return this.isInCalc;
	}

	protected void beginCalc() {
		this.isInCalc = true;
	}

	protected void endCalc() {
		this.isInCalc = false;
	}

	public void calcAll() {
		if (this.isInCalc()) {
			return;
		}
		try {
			this.beginCalc();
			this.doCalcAll();
			((AggregateSPI) this.getAggregate()).calcAll();
		} finally {
			this.endCalc();
		}
	}

	protected void doCalcAll() {
	}

}
