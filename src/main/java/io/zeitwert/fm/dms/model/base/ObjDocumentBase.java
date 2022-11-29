
package io.zeitwert.fm.dms.model.base;

import org.jooq.UpdatableRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
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

public abstract class ObjDocumentBase extends FMObjBase implements ObjDocument {

	protected static final Logger logger = LoggerFactory.getLogger(ObjDocumentBase.class);

	private final UpdatableRecord<?> dbRecord;

	protected final SimpleProperty<String> name;
	protected final EnumProperty<CodeDocumentKind> documentKind;
	protected final EnumProperty<CodeDocumentCategory> documentCategory;
	protected final ReferenceProperty<ObjDocument> templateDocument;
	protected final EnumProperty<CodeContentKind> contentKind;

	protected CodeContentType contentType;
	protected byte[] content;

	protected ObjDocumentBase(ObjDocumentRepository repository, UpdatableRecord<?> objRecord,
			UpdatableRecord<?> documentRecord) {
		super(repository, objRecord);
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
	public void doAfterLoad() {
		super.doAfterLoad();
		this.loadContent();
	}

	@Override
	public CodeContentType getContentType() {
		return this.contentType;
	}

	@Override
	public byte[] getContent() {
		return this.content;
	}

	@Override
	public void storeContent(CodeContentType contentType, byte[] content) {
		this.getRepository().storeContent(this.getRequestContext(), this, contentType, content);
		this.contentType = contentType;
		this.content = content;
		this.calcAll();
	}

	@Override
	public void doInit(Integer objId, Integer tenantId) {
		super.doInit(objId, tenantId);
		this.dbRecord.setValue(ObjDocumentFields.OBJ_ID, objId);
		this.dbRecord.setValue(ObjDocumentFields.TENANT_ID, tenantId);
	}

	@Override
	public void doStore() {
		super.doStore();
		this.dbRecord.store();
	}

	@Override
	public void doAfterStore() {
		super.doAfterStore();
		this.loadContent();
	}

	private void loadContent() {
		this.contentType = this.getRepository().getContentType(this);
		this.content = this.getRepository().getContent(this);
	}

	@Override
	protected void doCalcAll() {
		super.doCalcAll();
		this.calcCaption();
	}

	protected void calcCaption() {
		this.setCaption(this.getName());
	}

}
