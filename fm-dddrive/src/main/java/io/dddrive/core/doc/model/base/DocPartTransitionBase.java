package io.dddrive.core.doc.model.base;

import java.time.OffsetDateTime;

import io.dddrive.core.ddd.model.PartRepository;
import io.dddrive.core.doc.model.Doc;
import io.dddrive.core.doc.model.DocPartTransition;
import io.dddrive.core.doc.model.enums.CodeCaseStage;
import io.dddrive.core.oe.model.ObjUser;
import io.dddrive.core.property.model.BaseProperty;
import io.dddrive.core.property.model.EnumProperty;
import io.dddrive.core.property.model.Property;
import io.dddrive.core.property.model.ReferenceProperty;

public abstract class DocPartTransitionBase extends DocPartBase<Doc> implements DocPartTransition {

	protected final BaseProperty<Object> tenantId = this.addBaseProperty("tenantId", Object.class);
	protected final ReferenceProperty<ObjUser> user = this.addReferenceProperty("user", ObjUser.class);
	protected final BaseProperty<OffsetDateTime> timestamp = this.addBaseProperty("timestamp", OffsetDateTime.class);
	protected final EnumProperty<CodeCaseStage> oldCaseStage = this.addEnumProperty("oldCaseStage", CodeCaseStage.class);
	protected final EnumProperty<CodeCaseStage> newCaseStage = this.addEnumProperty("newCaseStage", CodeCaseStage.class);

	public DocPartTransitionBase(Doc doc, PartRepository<Doc, DocPartTransition> repository, Property<?> property, Integer id) {
		super(doc, repository, property, id);
	}

	@Override
	@SuppressWarnings("unchecked")
	public PartRepository<Doc, DocPartTransition> getRepository() {
		return (PartRepository<Doc, DocPartTransition>) super.getRepository();
	}

	@Override
	public void doAfterCreate() {
		//super.doAfterCreate();
		this.tenantId.setValue(this.getAggregate().getTenantId());
		this.oldCaseStage.setValue(null);
		this.newCaseStage.setValue(null);
	}

}
