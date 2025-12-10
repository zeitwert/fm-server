package io.dddrive.core.obj.model.base;

import java.time.OffsetDateTime;

import io.dddrive.core.ddd.model.PartRepository;
import io.dddrive.core.obj.model.Obj;
import io.dddrive.core.obj.model.ObjPartTransition;
import io.dddrive.core.oe.model.ObjUser;
import io.dddrive.core.property.model.BaseProperty;
import io.dddrive.core.property.model.Property;
import io.dddrive.core.property.model.ReferenceProperty;

public abstract class ObjPartTransitionBase extends ObjPartBase<Obj> implements ObjPartTransition {

	protected final BaseProperty<Object> tenantId = this.addBaseProperty("tenantId", Object.class);
	protected final ReferenceProperty<ObjUser> user = this.addReferenceProperty("user", ObjUser.class);
	protected final BaseProperty<OffsetDateTime> timestamp = this.addBaseProperty("timestamp", OffsetDateTime.class);

	public ObjPartTransitionBase(Obj obj, PartRepository<Obj, ObjPartTransition> repository, Property<?> property, Integer id) {
		super(obj, repository, property, id);
	}

	@Override
	@SuppressWarnings("unchecked")
	public PartRepository<Obj, ObjPartTransition> getRepository() {
		return (PartRepository<Obj, ObjPartTransition>) super.getRepository();
	}

	@Override
	public void doAfterCreate() {
		//super.doAfterCreate();
		this.tenantId.setValue(this.getAggregate().getTenantId());
	}

}
