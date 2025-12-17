package io.dddrive.core.doc.model.base;

import io.dddrive.core.ddd.model.PartRepository;
import io.dddrive.core.doc.model.Doc;
import io.dddrive.core.doc.model.DocPartTransition;
import io.dddrive.core.doc.model.enums.CodeCaseStage;
import io.dddrive.core.oe.model.ObjUser;
import io.dddrive.core.property.model.BaseProperty;
import io.dddrive.core.property.model.EnumProperty;
import io.dddrive.core.property.model.Property;
import io.dddrive.core.property.model.ReferenceProperty;

import java.time.OffsetDateTime;

public abstract class DocPartTransitionBase extends DocPartBase<Doc> implements DocPartTransition {

	protected final BaseProperty<Object> _tenantId = this.addBaseProperty("tenantId", Object.class);
	protected final ReferenceProperty<ObjUser> _user = this.addReferenceProperty("user", ObjUser.class);
	protected final BaseProperty<OffsetDateTime> _timestamp = this.addBaseProperty("timestamp", OffsetDateTime.class);
	protected final EnumProperty<CodeCaseStage> _oldCaseStage = this.addEnumProperty("oldCaseStage", CodeCaseStage.class);
	protected final EnumProperty<CodeCaseStage> _newCaseStage = this.addEnumProperty("newCaseStage", CodeCaseStage.class);

	public DocPartTransitionBase(Doc doc, PartRepository<Doc, DocPartTransition> repository, Property<?> property, int id) {
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
		this._tenantId.setValue(this.getAggregate().getTenantId());
		this._oldCaseStage.setValue(null);
		this._newCaseStage.setValue(null);
	}

}
