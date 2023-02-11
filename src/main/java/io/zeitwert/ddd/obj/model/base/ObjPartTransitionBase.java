package io.zeitwert.ddd.obj.model.base;

import java.time.OffsetDateTime;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPartTransition;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.session.model.RequestContext;

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
