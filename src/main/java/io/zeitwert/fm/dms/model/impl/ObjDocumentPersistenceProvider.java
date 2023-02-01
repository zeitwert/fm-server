package io.zeitwert.fm.dms.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.Configuration;

import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.model.base.ObjDocumentBase;
import io.zeitwert.fm.dms.model.db.Tables;
import io.zeitwert.fm.dms.model.db.tables.records.ObjDocumentRecord;
import io.zeitwert.fm.obj.model.base.FMObjPersistenceProviderBase;

@Configuration("documentPersistenceProvider")
public class ObjDocumentPersistenceProvider extends FMObjPersistenceProviderBase<ObjDocument> {

	public ObjDocumentPersistenceProvider(DSLContext dslContext) {
		super(ObjDocumentRepository.class, ObjDocumentBase.class, dslContext);
		this.mapField("name", DbTableType.EXTN, "name", String.class);
		this.mapField("documentKind", DbTableType.EXTN, "document_kind_id", String.class);
		this.mapField("documentCategory", DbTableType.EXTN, "document_category_id", String.class);
		this.mapField("templateDocument", DbTableType.EXTN, "template_document_id", Integer.class);
		this.mapField("contentKind", DbTableType.EXTN, "content_kind_id", String.class);
	}

	@Override
	public Class<?> getEntityClass() {
		return ObjDocument.class;
	}

	@Override
	public boolean isReal() {
		return true;
	}

	@Override
	public ObjDocument doCreate() {
		return this.doCreate(this.getDSLContext().newRecord(Tables.OBJ_DOCUMENT));
	}

	@Override
	public ObjDocument doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjDocumentRecord documentRecord = this.getDSLContext().fetchOne(Tables.OBJ_DOCUMENT,
				Tables.OBJ_DOCUMENT.OBJ_ID.eq(objId));
		if (documentRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(objId, documentRecord);
	}

}
