package io.zeitwert.ddd.doc.model.base;

import java.time.OffsetDateTime;

import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.DocPartTransition;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStage;
import io.zeitwert.ddd.oe.model.ObjUser;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.session.model.RequestContext;

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
