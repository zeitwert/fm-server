package io.dddrive.obj.model.base;

import java.time.OffsetDateTime;

import io.dddrive.app.model.RequestContext;
import io.dddrive.ddd.model.PartRepository;
import io.dddrive.obj.model.Obj;
import io.dddrive.obj.model.ObjPartTransition;
import io.dddrive.oe.model.ObjUser;
import io.dddrive.property.model.ReferenceProperty;
import io.dddrive.property.model.SimpleProperty;

public abstract class ObjPartTransitionBase extends ObjPartBase<Obj> implements ObjPartTransition {

	protected final SimpleProperty<Integer> tenantId = this.addSimpleProperty("tenantId", Integer.class);
	protected final ReferenceProperty<ObjUser> user = this.addReferenceProperty("user", ObjUser.class);
	protected final SimpleProperty<OffsetDateTime> timestamp = this.addSimpleProperty("timestamp", OffsetDateTime.class);

	public ObjPartTransitionBase(PartRepository<Obj, ?> repository, Obj obj, Object state) {
		super(repository, obj, state);
	}

	@Override
	public void doAfterCreate() {
		super.doAfterCreate();
		RequestContext requestCtx = this.getMeta().getRequestContext();
		this.tenantId.setValue(this.getAggregate().getTenantId());
		this.user.setValue(requestCtx.getUser());
		this.timestamp.setValue(requestCtx.getCurrentTime());
	}

}
