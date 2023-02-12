
package io.zeitwert.fm.dms.model.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dddrive.obj.model.base.ObjExtnBase;
import io.dddrive.property.model.EnumProperty;
import io.dddrive.property.model.ReferenceProperty;
import io.dddrive.property.model.SimpleProperty;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.service.api.ObjAccountCache;
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.model.enums.CodeContentKind;
import io.zeitwert.fm.dms.model.enums.CodeContentType;
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory;
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind;
import io.zeitwert.fm.task.model.impl.AggregateWithTasksMixin;

public abstract class ObjDocumentBase extends ObjExtnBase
		implements ObjDocument, AggregateWithNotesMixin, AggregateWithTasksMixin {

	protected static final Logger logger = LoggerFactory.getLogger(ObjDocumentBase.class);

	//@formatter:off
	protected final SimpleProperty<String> name = this.addSimpleProperty("name", String.class);
	protected final EnumProperty<CodeDocumentKind> documentKind = this.addEnumProperty("documentKind", CodeDocumentKind.class);
	protected final EnumProperty<CodeDocumentCategory> documentCategory = this.addEnumProperty("documentCategory", CodeDocumentCategory.class);
	protected final ReferenceProperty<ObjDocument> templateDocument = this.addReferenceProperty("templateDocument", ObjDocument.class);
	protected final EnumProperty<CodeContentKind> contentKind = this.addEnumProperty("contentKind", CodeContentKind.class);
	//@formatter:on

	protected CodeContentType contentType;
	protected byte[] content;

	protected ObjDocumentBase(ObjDocumentRepository repository, Object state) {
		super(repository, state);
	}

	@Override
	public ObjDocumentRepository getRepository() {
		return (ObjDocumentRepository) super.getRepository();
	}

	@Override
	public ObjDocument aggregate() {
		return this;
	}

	@Override
	public void doAfterLoad() {
		super.doAfterLoad();
		this.loadContent();
	}

	@Override
	public final ObjAccount getAccount() {
		return this.getAppContext().getBean(ObjAccountCache.class).get(this.getAccountId());
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
	public void doCalcSearch() {
		this.addSearchText(this.getName());
	}

	@Override
	public void doAfterStore() {
		super.doAfterStore();
		this.loadContent();
	}

	private void loadContent() {
		this.contentType = this.getRepository().getContentType(this);
		if (this.contentType != null) {
			this.content = this.getRepository().getContent(this);
		}
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
