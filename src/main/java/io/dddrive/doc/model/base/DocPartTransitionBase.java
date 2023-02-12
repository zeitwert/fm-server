package io.dddrive.doc.model.base;

import java.time.OffsetDateTime;

import io.dddrive.app.model.RequestContext;
import io.dddrive.ddd.model.PartRepository;
import io.dddrive.doc.model.Doc;
import io.dddrive.doc.model.DocPartTransition;
import io.dddrive.doc.model.enums.CodeCaseStage;
import io.dddrive.oe.model.ObjUser;
import io.dddrive.property.model.EnumProperty;
import io.dddrive.property.model.ReferenceProperty;
import io.dddrive.property.model.SimpleProperty;

public abstract class DocPartTransitionBase extends DocPartBase<Doc> implements DocPartTransition {

	protected final SimpleProperty<Integer> tenantId = this.addSimpleProperty("tenantId", Integer.class);
	protected final ReferenceProperty<ObjUser> user = this.addReferenceProperty("user", ObjUser.class);
	protected final SimpleProperty<OffsetDateTime> timestamp = this.addSimpleProperty("timestamp", OffsetDateTime.class);
	protected final EnumProperty<CodeCaseStage> oldCaseStage = this.addEnumProperty("oldCaseStage", CodeCaseStage.class);
	protected final EnumProperty<CodeCaseStage> newCaseStage = this.addEnumProperty("newCaseStage", CodeCaseStage.class);

	public DocPartTransitionBase(PartRepository<Doc, ?> repository, Doc doc, Object state) {
		super(repository, doc, state);
	}

	@Override
	public void doAfterCreate() {
		super.doAfterCreate();
		RequestContext requestCtx = this.getMeta().getRequestContext();
		this.tenantId.setValue(this.getAggregate().getTenantId());
		this.user.setValue(requestCtx.getUser());
		this.timestamp.setValue(requestCtx.getCurrentTime());
		this.oldCaseStage.setValue(null);
		this.newCaseStage.setValue(null);
	}

}
