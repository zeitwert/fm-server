
package io.zeitwert.ddd.aggregate.model.base;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateMeta;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesBase;
import io.zeitwert.ddd.validation.model.AggregatePartValidation;
import io.zeitwert.ddd.validation.model.enums.CodeValidationLevel;
import io.zeitwert.ddd.validation.model.impl.AggregatePartValidationImpl;

import java.util.ArrayList;
import java.util.List;

import org.jooq.Record;

import static io.zeitwert.ddd.util.Check.assertThis;

/**
 * An Aggregate based on jOOQ Record
 */
public abstract class AggregateBase extends EntityWithPropertiesBase implements Aggregate, AggregateMeta, AggregateSPI {

	private Boolean isStale = false;
	private boolean isInCalc = false;
	private List<AggregatePartValidation> validationList = new ArrayList<>();

	private boolean didCalcAll = false;
	private boolean didCalcVolatile = false;

	protected Integer doInitSeqNr = 0;
	protected Integer doAfterCreateSeqNr = 0;
	protected Integer doAssignPartsSeqNr = 0;
	protected Integer doAfterLoadSeqNr = 0;
	protected Integer doBeforeStoreSeqNr = 0;
	protected Integer doStoreSeqNr = 0;
	protected Integer doAfterStoreSeqNr = 0;

	@Override
	public AppContext getAppContext() {
		return AppContext.getInstance();
	}

	@Override
	public abstract AggregateRepository<? extends Aggregate, ? extends Record> getRepository();

	public AggregateMeta getMeta() {
		return this;
	}

	Boolean isStale() {
		return this.isStale;
	}

	void setStale() {
		this.isStale = true;
	}

	@Override
	public void doInit(Integer aggregateId, Integer tenantId) {
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
	public void doBeforeStore() {
		this.doBeforeStoreSeqNr += 1;
		this.doBeforeStoreProperties();
	}

	@Override
	public void doStore() {
		this.doStoreSeqNr += 1;
	}

	@Override
	public void doAfterStore() {
		this.doAfterStoreSeqNr += 1;
	}

	@Override
	public void delete() {
		assertThis(false, "delete supported");
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

	private void clearValidationList() {
		this.validationList.clear();
	}

	@Override
	public List<AggregatePartValidation> getValidationList() {
		return List.copyOf(this.validationList);
	}

	protected void addValidation(CodeValidationLevel validationLevel, String validation) {
		this.validationList.add(AggregatePartValidationImpl.builder().seqNr(this.validationList.size())
				.validationLevel(validationLevel).validation(validation).build());
	}

	protected Boolean isInCalc() {
		return this.isInCalc;
	}

	protected void beginCalc() {
		this.isInCalc = true;
		this.clearValidationList();
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
			this.didCalcAll = false;
			this.doCalcAll();
			assertThis(this.didCalcAll, this.getClass().getSimpleName() + ": doCalcAll was propagated");
		} finally {
			this.endCalc();
		}
	}

	protected void doCalcAll() {
		this.didCalcAll = true;
	}

	public void calcVolatile() {
		if (this.isInCalc()) {
			return;
		}
		try {
			this.beginCalc();
			this.didCalcVolatile = false;
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
