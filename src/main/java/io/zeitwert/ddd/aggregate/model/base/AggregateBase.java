
package io.zeitwert.ddd.aggregate.model.base;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.aggregate.model.AggregateMeta;
import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.enums.model.Enumerated;
import io.zeitwert.ddd.enums.model.Enumeration;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.base.EntityWithPropertiesBase;
import io.zeitwert.ddd.validation.model.AggregatePartValidation;
import io.zeitwert.ddd.validation.model.enums.CodeValidationLevel;
import io.zeitwert.ddd.validation.model.impl.AggregatePartValidationImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jooq.Record;

/**
 * An Aggregate based on jOOQ Record
 */
public abstract class AggregateBase extends EntityWithPropertiesBase implements Aggregate, AggregateMeta, AggregateSPI {

	private Boolean isStale = false;
	private boolean isInCalc = false;
	private List<AggregatePartValidation> validationList = new ArrayList<>();

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

	protected <E extends Enumerated> boolean isValidEnum(E item, Class<? extends Enumeration<E>> enumClass) {
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

	@Override
	public abstract void doInit(Integer aggregateId, Integer tenantId);

	@Override
	public void doAfterCreate() {
	}

	@Override
	public void doAfterLoad() {
	}

	@Override
	public void doBeforeStore() {
		this.doBeforeStoreProperties();
	}

	@Override
	public void doAfterStore() {
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
			this.doCalcAll();
		} finally {
			this.endCalc();
		}
	}

	protected void doCalcAll() {
	}

	public void calcVolatile() {
		if (this.isInCalc()) {
			return;
		}
		try {
			this.beginCalc();
			this.doCalcVolatile();
		} finally {
			this.endCalc();
		}
	}

	protected void doCalcVolatile() {
	}

}
