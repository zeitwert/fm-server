package io.dddrive.core.ddd.model.base;

import static io.dddrive.util.Invariant.assertThis;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.lang.Nullable;

import io.dddrive.core.ddd.model.Aggregate;
import io.dddrive.core.ddd.model.AggregateMeta;
import io.dddrive.core.ddd.model.AggregateRepository;
import io.dddrive.core.ddd.model.AggregateSPI;
import io.dddrive.core.ddd.model.Part;
import io.dddrive.core.oe.model.ObjTenant;
import io.dddrive.core.oe.model.ObjUser;
import io.dddrive.core.property.model.BaseProperty;
import io.dddrive.core.property.model.EntityWithPropertiesSPI;
import io.dddrive.core.property.model.Property;
import io.dddrive.core.property.model.PropertyChangeListener;
import io.dddrive.core.property.model.ReferenceProperty;
import io.dddrive.core.validation.model.AggregatePartValidation;
import io.dddrive.core.validation.model.enums.CodeValidationLevel;
import io.dddrive.core.validation.model.impl.AggregatePartValidationImpl;

/**
 * A DDD Aggregate
 */
public abstract class AggregateBase extends AggregateWithRepositoryBase implements Aggregate, AggregateMeta, AggregateSPI {

	//@formatter:off
	protected final BaseProperty<Object> id = this.addBaseProperty("id", Object.class);
	protected final BaseProperty<Integer> maxPartId = this.addBaseProperty("maxPartId", Integer.class);
	protected final BaseProperty<Integer> version = this.addBaseProperty("version", Integer.class);
	protected final ReferenceProperty<ObjTenant> tenant = this.addReferenceProperty("tenant", ObjTenant.class);
	protected final ReferenceProperty<ObjUser> owner = this.addReferenceProperty("owner", ObjUser.class);
	protected final BaseProperty<String> caption = this.addBaseProperty("caption", String.class);
	protected final ReferenceProperty<ObjUser> createdByUser = this.addReferenceProperty("createdByUser", ObjUser.class);
	protected final BaseProperty<OffsetDateTime> createdAt = this.addBaseProperty("createdAt", OffsetDateTime.class);
	protected final ReferenceProperty<ObjUser> modifiedByUser = this.addReferenceProperty("modifiedByUser", ObjUser.class);
	protected final BaseProperty<OffsetDateTime> modifiedAt = this.addBaseProperty("modifiedAt", OffsetDateTime.class);
	//@formatter:on

	private final Set<PropertyChangeListener> propertyChangeListeners = new HashSet<>();
	private final List<AggregatePartValidation> validations = new ArrayList<>();
	protected Integer doCreateSeqNr = 0;
	protected Integer doAfterCreateSeqNr = 0;
	protected Integer doAfterLoadSeqNr = 0;
	protected Integer doBeforeStoreSeqNr = 0;
	protected Integer doAfterStoreSeqNr = 0;
	private boolean isFrozen = false;
	private boolean isInLoad = false;
	private int isCalcDisabled = 0;
	private boolean isInCalc = false;
	private boolean didCalcAll = false;
	private boolean didCalcVolatile = false;

	protected AggregateBase(AggregateRepository<? extends Aggregate> repository) {
		super(repository);
	}

	@Override
	public String toString() {
		return this.getCaption();
	}

	@Override
	public AggregateMeta getMeta() {
		return this;
	}

	@Override
	public String getRelativePath() {
		return "";
	}

	@Override
	public String getPath() {
		return this.getRepository().getAggregateType().getId() + "(" + this.getId() + ")";
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.propertyChangeListeners.add(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.propertyChangeListeners.remove(listener);
	}

	@Override
	public void fireFieldChange(String op, String path, String value, String oldValue, boolean isInCalc) {
		this.propertyChangeListeners.forEach(listener -> listener.propertyChange(op, path, value, oldValue, isInCalc));
	}

	@Override
	protected boolean doLogChange(String propertyName) {
		return this.getRepository().doLogChange(propertyName);
	}

	@Override
	public <P extends Part<?>> int nextPartId(Class<P> partClass) {
		synchronized (this.maxPartId) {
			Integer maxPartId = this.maxPartId.getValue();
			this.maxPartId.setValue((maxPartId == null ? 0 : maxPartId) + 1);
			return this.maxPartId.getValue();
		}
	}

	@Override
	public void doCreate(Object aggregateId, Object tenantId, Object userId, OffsetDateTime timestamp) {
		this.fireEntityAddedChange(aggregateId);
		this.id.setValue(aggregateId);
		this.tenant.setId(tenantId);
		this.createdByUser.setId(userId);
		this.createdAt.setValue(timestamp);
		this.doCreateSeqNr += 1;
	}

	@Override
	public void doAfterCreate(Object userId, OffsetDateTime timestamp) {
		this.doAfterCreateSeqNr += 1;
	}

	@Override
	public void doAfterLoad() {
		this.doAfterLoadSeqNr += 1;
	}

	@Override
	public void doBeforeStore(Object userId, OffsetDateTime timestamp) {
		this.doBeforeStoreSeqNr += 1;
	}

	@Override
	public void doAfterStore() {
		this.doAfterStoreSeqNr += 1;
	}

	@Override
	public boolean isFrozen() {
		return this.isFrozen;
	}

	protected void unfreeze() {
		this.isFrozen = false;
	}

	protected void freeze() {
		this.isFrozen = true;
	}

	@Override
	public Part<?> doAddPart(Property<?> property, Integer partId) {
		throw new RuntimeException("did not instantiate part for property " + this.getClassName() + "." + property.getName());
	}

	@Override
	public void doBeforeSet(Property<?> property, @Nullable Object value, @Nullable Object oldValue) {
	}

	@Override
	public void doAfterSet(Property<?> property) {
		this.calcAll();
	}

	@Override
	public void doAfterAdd(Property<?> property, Part<?> part) {
		if (part != null) {
			this.addPart(part);
		}
		this.calcAll();
	}

	@Override
	public void doAfterRemove(Property<?> property) {
		this.calcAll();
	}

	@Override
	public void doAfterClear(Property<?> property) {
		this.calcAll();
	}

	private void clearValidationList() {
		this.validations.clear();
	}

	@Override
	public List<AggregatePartValidation> getValidations() {
		return List.copyOf(this.validations);
	}

	protected void addValidation(CodeValidationLevel validationLevel, String validation) {
		this.addValidation(validationLevel, validation, (String) null);
	}

	protected void addValidation(CodeValidationLevel validationLevel, String validation, EntityWithPropertiesSPI entity) {
		this.addValidation(validationLevel, validation, entity.getRelativePath());
	}

	protected void addValidation(CodeValidationLevel validationLevel, String validation, Property<?> property) {
		this.addValidation(validationLevel, validation, property.getRelativePath());
	}

	protected void addValidation(CodeValidationLevel validationLevel, String validation, @Nullable String path) {
		this.validations.add(new AggregatePartValidationImpl(this.validations.size(), validationLevel, validation, path));
	}

	@Override
	public boolean isInLoad() {
		return this.isInLoad;
	}

	@Override
	public void beginLoad() {
		this.isInLoad = true;
	}

	@Override
	public void endLoad() {
		this.isInLoad = false;
	}

	@Override
	public boolean isCalcEnabled() {
		return this.isCalcDisabled == 0;
	}

	@Override
	public void disableCalc() {
		this.isCalcDisabled += 1;
	}

	@Override
	public void enableCalc() {
		this.isCalcDisabled -= 1;
	}

	@Override
	public boolean isInCalc() {
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
			this.clearValidationList();
			this.doCalcAll();
			assertThis(this.didCalcAll, this.getClassName() + ": doCalcAll was propagated");
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
			assertThis(this.didCalcVolatile, this.getClassName() + ": doCalcAll was propagated");
		} finally {
			this.endCalc();
		}
	}

	protected void doCalcVolatile() {
		this.didCalcVolatile = true;
	}

	private String getClassName() {
		return this.getClass().getSuperclass().getSimpleName();
	}

}
