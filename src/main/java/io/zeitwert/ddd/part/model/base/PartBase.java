package io.zeitwert.ddd.part.model.base;

import static io.zeitwert.ddd.util.Check.assertThis;

import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.PartMeta;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesBase;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.ddd.session.model.RequestContext;

public abstract class PartBase<A extends Aggregate> extends EntityWithPropertiesBase
		implements Part<A>, PartMeta<A>, PartSPI<A> {

	private final PartRepository<A, ?> repository;

	private A aggregate;
	private final UpdatableRecord<?> dbRecord;

	protected final SimpleProperty<Integer> id;
	protected final SimpleProperty<Integer> parentPartId;
	protected final SimpleProperty<String> partListTypeId;
	protected final SimpleProperty<Integer> seqNr;

	private boolean isDeleted = false;
	private int isCalcDisabled = 0;
	private boolean isInCalc = false;

	private boolean didCalcAll = false;
	private boolean didCalcVolatile = false;

	protected Integer doInitSeqNr = 0;
	protected Integer doAfterCreateSeqNr = 0;
	protected Integer doAssignPartsSeqNr = 0;
	protected Integer doAfterLoadSeqNr = 0;
	protected Integer doBeforeStoreSeqNr = 0;
	protected Integer doStoreSeqNr = 0;
	protected Integer doAfterStoreSeqNr = 0;

	protected PartBase(PartRepository<A, ?> repository, A aggregate, UpdatableRecord<?> dbRecord) {
		this.repository = repository;
		this.aggregate = aggregate;
		this.dbRecord = dbRecord;
		this.id = this.addSimpleProperty(dbRecord, PartFields.ID);
		this.parentPartId = this.addSimpleProperty(dbRecord, PartFields.PARENT_PART_ID);
		this.partListTypeId = this.addSimpleProperty(dbRecord, PartFields.PART_LIST_TYPE_ID);
		this.seqNr = this.addSimpleProperty(dbRecord, PartFields.SEQ_NR);
	}

	public PartRepository<A, ?> getRepository() {
		return this.repository;
	}

	public PartMeta<A> getMeta() {
		return this;
	}

	@Override
	public AppContext getAppContext() {
		return this.getAggregate().getMeta().getAppContext();
	}

	@Override
	public RequestContext getRequestContext() {
		return this.getAggregate().getMeta().getRequestContext();
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

	public void setSeqNr(Integer seqNr) {
		try {
			this.disableCalc(); // suppress calc
			this.seqNr.setValue(seqNr);
		} catch (IllegalArgumentException e) {
		} finally {
			this.enableCalc();
		}
	}

	@Override
	public void doInit(Integer partId, A aggregate, Part<?> parent, CodePartListType partListType) {
		this.doInitSeqNr += 1;
	}

	@Override
	public void doAfterCreate() {
		this.doAfterCreateSeqNr += 1;
	}

	@Override
	public void doAssignParts() {
		this.doAssignPartsSeqNr += 1;
	}

	@Override
	public void doAfterLoad() {
		this.doAfterLoadSeqNr += 1;
	}

	@Override
	public PartStatus getStatus() {
		PartRepositorySPI<?, ?> repoSpi = (PartRepositorySPI<?, ?>) this.getRepository();
		if (this.isDeleted) {
			return PartStatus.DELETED;
		} else if (repoSpi.hasPartId() && this.getDbRecord().changed(PartFields.ID)) {
			return PartStatus.CREATED;
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
	public void doBeforeStore() {
		this.doBeforeStoreSeqNr += 1;
		this.doBeforeStoreProperties();
	}

	@Override
	public final void doStore() {
		this.doStoreSeqNr += 1;
		if (this.getStatus() == PartStatus.DELETED) {
			this.getDbRecord().delete();
		} else if (this.getStatus() != PartStatus.READ) {
			this.getDbRecord().store();
		}
	}

	@Override
	public void doAfterStore() {
		this.doAfterStoreSeqNr += 1;
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

	@Override
	public void afterClear(Property<?> property) {
		this.calcAll();
	}

	@Override
	public boolean isCalcEnabled() {
		return this.isCalcDisabled == 0 && this.getAggregate().getMeta().isCalcEnabled();
	}

	@Override
	public void disableCalc() {
		this.isCalcDisabled += 1;
	}

	@Override
	public void enableCalc() {
		this.isCalcDisabled -= 1;
	}

	protected Boolean isInCalc() {
		return this.isInCalc;
	}

	protected void beginCalc() {
		this.isInCalc = true;
		this.didCalcAll = false;
		this.didCalcVolatile = false;
	}

	protected void endCalc() {
		this.isInCalc = false;
	}

	@Override
	public void calcAll() {
		if (!this.isCalcEnabled() || this.isInCalc()) {
			return;
		}
		try {
			this.beginCalc();
			this.doCalcAll();
			this.getAggregate().calcAll();
			assertThis(this.didCalcAll, this.getClass().getSimpleName() + ": doCalcAll was propagated");
		} finally {
			this.endCalc();
		}
	}

	protected void doCalcAll() {
		this.didCalcAll = true;
	}

	@Override
	public void calcVolatile() {
		if (!this.isCalcEnabled() || this.isInCalc()) {
			return;
		}
		try {
			this.beginCalc();
			this.doCalcVolatile();
			assertThis(this.didCalcVolatile, this.getClass().getSimpleName() + ": doCalcAll was propagated");
		} finally {
			this.endCalc();
		}
	}

	protected void doCalcVolatile() {
		this.didCalcVolatile = true;
	}

}
