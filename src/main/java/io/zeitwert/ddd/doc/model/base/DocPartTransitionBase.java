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
import io.zeitwert.jooq.persistence.PartState;

import org.jooq.JSON;

public abstract class DocPartTransitionBase extends DocPartBase<Doc> implements DocPartTransition {

	protected final SimpleProperty<Integer> tenantId = this.addSimpleProperty("tenantId", Integer.class);
	protected final ReferenceProperty<ObjUser> user = this.addReferenceProperty("user", ObjUser.class);
	protected final SimpleProperty<OffsetDateTime> timestamp = this.addSimpleProperty("timestamp", OffsetDateTime.class);
	protected final EnumProperty<CodeCaseStage> oldCaseStage = this.addEnumProperty("oldCaseStage", CodeCaseStage.class);
	protected final EnumProperty<CodeCaseStage> newCaseStage = this.addEnumProperty("newCaseStage", CodeCaseStage.class);
	protected final SimpleProperty<JSON> changes = this.addSimpleProperty("changes", JSON.class);

	public DocPartTransitionBase(PartRepository<Doc, ?> repository, Doc doc, PartState state) {
		super(repository, doc, state);
	}

	@Override
	public void doAfterCreate() {
		super.doAfterCreate();
		RequestContext requestCtx = this.getMeta().getRequestContext();
		tenantId.setValue(this.getAggregate().getTenantId());
		user.setValue(requestCtx.getUser());
		timestamp.setValue(requestCtx.getCurrentTime());
		oldCaseStage.setValue(null);
		newCaseStage.setValue(null);
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
