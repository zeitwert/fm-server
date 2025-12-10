package io.dddrive.core.ddd.model.base;

import static io.dddrive.util.Invariant.assertThis;

import org.springframework.lang.Nullable;

import io.dddrive.core.ddd.model.Aggregate;
import io.dddrive.core.ddd.model.Part;
import io.dddrive.core.ddd.model.PartMeta;
import io.dddrive.core.ddd.model.PartRepository;
import io.dddrive.core.ddd.model.PartSPI;
import io.dddrive.core.ddd.model.RepositoryDirectory;
import io.dddrive.core.property.model.BaseProperty;
import io.dddrive.core.property.model.EntityWithPropertiesSPI;
import io.dddrive.core.property.model.PartListProperty;
import io.dddrive.core.property.model.Property;
import io.dddrive.core.property.model.base.EntityWithPropertiesBase;

public abstract class PartBase<A extends Aggregate>
		extends EntityWithPropertiesBase
		implements Part<A>, PartMeta<A>, PartSPI<A> {

	protected final BaseProperty<Integer> id = this.addBaseProperty("id", Integer.class);

	private final A aggregate;
	private final PartRepository<A, ? extends Part<A>> repository;
	private final Property<?> parentProperty;
	private boolean isChanged = false;
	private int isCalcDisabled = 0;
	private boolean isInCalc = false;
	private boolean didCalcAll = false;
	private boolean didCalcVolatile = false;

	protected PartBase(A aggregate, PartRepository<A, ? extends Part<A>> repository, Property<?> parentProperty, Integer id) {
		this.aggregate = aggregate;
		this.repository = repository;
		this.parentProperty = parentProperty;
		this.id.setValue(id);
	}

	@Override
	public RepositoryDirectory getDirectory() {
		return this.getAggregate().getMeta().getRepository().getDirectory();
	}

	@Override
	public PartRepository<A, ? extends Part<A>> getRepository() {
		return this.repository;
	}

	@Override
	public A getAggregate() {
		return this.aggregate;
	}

	@Override
	public Integer getId() {
		return this.id.getValue();
	}

	@Override
	public boolean isInLoad() {
		return this.getAggregate().getMeta().isInLoad();
	}

	@Override
	public Property<?> getParentProperty() {
		return this.parentProperty;
	}

	private String buildPath(String basePath) {
		Property<?> parentProp = this.getParentProperty();
		if (parentProp instanceof PartListProperty<?> listProperty) {
			int index = listProperty.getIndexOfPart(this);
			if (index == -1) {
				index = listProperty.getPartCount();
			}
			return basePath + "[" + index + "]";
		} else {
			return basePath + "." + this.getId();
		}
	}

	@Override
	public String getRelativePath() {
		Property<?> parentProp = this.getParentProperty();
		String parentRelativePath = parentProp.getRelativePath();
		return buildPath(parentRelativePath);
	}

	@Override
	public String getPath() {
		Property<?> parentProp = this.getParentProperty();
		String parentPath = parentProp.getPath();
		return buildPath(parentPath);
	}

	@Override
	public void fireFieldChange(String op, String path, String value, String oldValue, boolean isInCalc) {
		((EntityWithPropertiesSPI) this.getAggregate()).fireFieldChange(op, path, value, oldValue, isInCalc);
	}

	@Override
	protected boolean doLogChange(String propertyName) {
		return this.getRepository().doLogChange(propertyName);
	}

	@Override
	public boolean hasPart(Integer partId) {
		return this.aggregate.hasPart(partId);
	}

	@Override
	public Part<?> getPart(Integer partId) {
		return this.aggregate.getPart(partId);
	}

	@Override
	public boolean isFrozen() {
		return this.getAggregate().isFrozen();
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
			((EntityWithPropertiesSPI) this.aggregate).doAfterAdd(property, part);
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

	@Override
	public boolean isChanged() {
		return this.isChanged;
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
		this.isChanged = true;
		if (!this.isCalcEnabled() || this.isInCalc()) {
			return;
		}
		try {
			this.beginCalc();
			this.doCalcAll();
			this.getAggregate().calcAll();
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
