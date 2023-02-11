package io.zeitwert.ddd.part.model.base;

import static io.zeitwert.ddd.util.Check.assertThis;

import java.util.List;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.PartMeta;
import io.zeitwert.ddd.part.model.PartPersistenceProvider;
import io.zeitwert.ddd.part.model.PartPersistenceStatus;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.PropertyProvider;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesBase;
import io.zeitwert.ddd.session.model.RequestContext;

public abstract class PartBase<A extends Aggregate> extends EntityWithPropertiesBase
		implements Part<A>, PartMeta<A>, PartSPI<A> {

	private final PartRepository<A, ?> repository;

	private A aggregate;
	private Object state;

	protected final SimpleProperty<Integer> id;
	protected final SimpleProperty<Integer> parentPartId = this.addSimpleProperty("parentPartId", Integer.class);
	protected final SimpleProperty<String> partListTypeId = this.addSimpleProperty("partListTypeId", String.class);
	protected final SimpleProperty<Integer> seqNr = this.addSimpleProperty("seqNr", Integer.class);

	private boolean isDeleted = false;
	private int isCalcDisabled = 0;
	private boolean isInCalc = false;

	private boolean didCalcAll = false;
	private boolean didCalcVolatile = false;

	protected Integer doAfterCreateSeqNr = 0;
	protected Integer doAssignPartsSeqNr = 0;
	protected Integer doAfterLoadSeqNr = 0;
	protected Integer doBeforeStoreSeqNr = 0;
	protected Integer doAfterStoreSeqNr = 0;

	protected PartBase(PartRepository<A, ?> repository, A aggregate, Object state) {
		this.repository = repository;
		this.aggregate = aggregate;
		this.state = state;
		// xyPartItem don't have an id
		if (((PartRepositorySPI<?, ?>) repository).hasPartId()) {
			this.id = this.addSimpleProperty("id", Integer.class);
		} else {
			this.id = null;
		}
	}

	@Override
	public PartRepository<A, ?> getRepository() {
		return this.repository;
	}

	@Override
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

	@Override
	public final PropertyProvider getPropertyProvider() {
		if (this.getRepository() != null) { // possibly null in instatiation phase
			return ((PartRepositorySPI<?, ?>) this.getRepository()).getPropertyProvider();
		}
		return null;
	}

	@Override
	public Object getPartState() {
		return this.state;
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
		return this.parentPartId.getValue();
	}

	@Override
	public String getPartListTypeId() {
		return this.partListTypeId.getValue();
	}

	@Override
	public Integer getSeqNr() {
		return this.seqNr.getValue();
	}

	public void setSeqNr(Integer seqNr) {
		try {
			this.disableCalc(); // suppress calc
			this.seqNr.setValue(seqNr);
		} finally {
			this.enableCalc();
		}
	}

	@Override
	public void doAfterCreate() {
		this.doAfterCreateSeqNr += 1;
	}

	@Override
	public void doAssignParts() {
		this.doAssignPartsSeqNr += 1;
		for (Property<?> property : this.getProperties()) {
			if (property instanceof PartListProperty<?>) {
				PartListProperty<?> partListProperty = (PartListProperty<?>) property;
				this.assignPartListParts(partListProperty, partListProperty.getPartListType());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <P extends Part<A>> void assignPartListParts(PartListProperty<?> property,
			CodePartListType partListType) {
		Class<P> partType = ((PartListProperty<P>) property).getPartType();
		PartRepository<A, P> partRepository = AppContext.getInstance().getPartRepository(partType);
		List<P> partList = partRepository.getParts(this, partListType);
		property.loadParts(partList);
	}

	@Override
	public void doAfterLoad() {
		this.doAfterLoadSeqNr += 1;
	}

	@Override
	@SuppressWarnings("unchecked")
	public PartPersistenceStatus getPersistenceStatus() {
		return ((PartPersistenceProvider<A, ?>) this.getPropertyProvider()).getPersistenceStatus(this);
	}

	@Override
	public boolean isDeleted() {
		return this.isDeleted;
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
	public void doAfterStore() {
		this.doAfterStoreSeqNr += 1;
	}

	@Override
	public Part<?> addPart(Property<?> property, CodePartListType partListType) {
		if (property instanceof PartListProperty<?>) {
			return this.addPartListPart(property, partListType);
		}
		assertThis(false, "could instantiate part for partListType " + partListType);
		return null;
	}

	@SuppressWarnings("unchecked")
	private <P extends Part<A>> Part<A> addPartListPart(Property<?> property,
			CodePartListType partListType) {
		Class<P> partType = ((PartListProperty<P>) property).getPartType();
		PartRepository<A, P> partRepository = AppContext.getInstance().getPartRepository(partType);
		return partRepository.create(this, partListType);
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
