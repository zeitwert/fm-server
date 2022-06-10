
package io.zeitwert.fm.dms.model.base;

import org.jooq.UpdatableRecord;

import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.model.enums.CodeContentKind;
import io.zeitwert.fm.dms.model.enums.CodeContentKindEnum;
import io.zeitwert.fm.dms.model.enums.CodeContentType;
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory;
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategoryEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind;
import io.zeitwert.fm.dms.model.enums.CodeDocumentKindEnum;
import io.zeitwert.fm.obj.model.base.FMObjBase;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.session.model.SessionInfo;

public abstract class ObjDocumentBase extends FMObjBase implements ObjDocument {

	private final UpdatableRecord<?> dbRecord;

	protected final SimpleProperty<String> name;
	protected final EnumProperty<CodeDocumentKind> documentKind;
	protected final EnumProperty<CodeDocumentCategory> documentCategory;
	protected final ReferenceProperty<ObjDocument> templateDocument;
	protected final EnumProperty<CodeContentKind> contentKind;

	protected ObjDocumentBase(SessionInfo sessionInfo, ObjDocumentRepository repository, UpdatableRecord<?> objRecord,
			UpdatableRecord<?> documentRecord) {
		super(sessionInfo, repository, objRecord);
		this.dbRecord = documentRecord;
		this.name = this.addSimpleProperty(dbRecord, ObjDocumentFields.NAME);
		this.documentKind = this.addEnumProperty(dbRecord, ObjDocumentFields.DOCUMENT_KIND_ID, CodeDocumentKindEnum.class);
		this.documentCategory = this.addEnumProperty(dbRecord, ObjDocumentFields.DOCUMENT_CATEGORY_ID,
				CodeDocumentCategoryEnum.class);
		this.templateDocument = this.addReferenceProperty(dbRecord, ObjDocumentFields.TEMPLATE_DOCUMENT_ID,
				ObjDocument.class);
		this.contentKind = this.addEnumProperty(dbRecord, ObjDocumentFields.CONTENT_KIND_ID, CodeContentKindEnum.class);
	}

	@Override
	public ObjDocumentRepository getRepository() {
		return (ObjDocumentRepository) super.getRepository();
	}

	@Override
	public CodeContentType getContentType() {
		return this.getRepository().getContentType(this);
	}

	@Override
	public byte[] getContent() {
		return this.getRepository().getContent(this);
	}

	@Override
	public void storeContent(CodeContentType contentType, byte[] content) {
		this.getRepository().storeContent(this.getSessionInfo(), this, contentType, content);
	}

	@Override
	public void doInit(Integer objId, Integer tenantId) {
		super.doInit(objId, tenantId);
		this.dbRecord.setValue(ObjDocumentFields.OBJ_ID, objId);
	}

	@Override
	public void doStore() {
		super.doStore();
		this.dbRecord.store();
	}

	@Override
	protected void doCalcAll() {
		this.calcCaption();
	}

	protected void calcCaption() {
		this.caption.setValue(this.getName());
	}

}
