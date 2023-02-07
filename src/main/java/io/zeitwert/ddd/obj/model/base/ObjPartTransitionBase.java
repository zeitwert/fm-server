package io.zeitwert.ddd.obj.model.base;

import java.time.OffsetDateTime;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjPartTransition;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.jooq.persistence.PartState;

import org.jooq.JSON;

public abstract class ObjPartTransitionBase extends ObjPartBase<Obj> implements ObjPartTransition {

	protected final SimpleProperty<Integer> tenantId = this.addSimpleProperty("tenantId", Integer.class);
	protected final ReferenceProperty<ObjUser> user = this.addReferenceProperty("user", ObjUser.class);
	protected final SimpleProperty<OffsetDateTime> timestamp = this.addSimpleProperty("timestamp", OffsetDateTime.class);
	protected final SimpleProperty<JSON> changes = this.addSimpleProperty("changes", JSON.class);

	public ObjPartTransitionBase(PartRepository<Obj, ?> repository, Obj obj, PartState state) {
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

	@Override
	public String getChanges() {
		JSON changes = this.changes.getValue();
		return changes != null ? changes.toString() : null;
	}

	public void setChanges(String changes) {
		this.changes.setValue(JSON.valueOf(changes));
	}

}
