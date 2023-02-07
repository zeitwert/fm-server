package io.zeitwert.fm.dms.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.db.Tables;
import io.zeitwert.fm.dms.model.db.tables.records.ObjDocumentRecord;
import io.zeitwert.jooq.persistence.AggregateState;
import io.zeitwert.jooq.persistence.ObjExtnPersistenceProviderBase;

@Configuration("documentPersistenceProvider")
@DependsOn("codePartListTypeEnum")
public class ObjDocumentPersistenceProvider extends ObjExtnPersistenceProviderBase<ObjDocument> {

	public ObjDocumentPersistenceProvider(DSLContext dslContext) {
		super(ObjDocument.class, dslContext);
		this.mapField("name", AggregateState.EXTN, "name", String.class);
		this.mapField("documentKind", AggregateState.EXTN, "document_kind_id", String.class);
		this.mapField("documentCategory", AggregateState.EXTN, "document_category_id", String.class);
		this.mapField("templateDocument", AggregateState.EXTN, "template_document_id", Integer.class);
		this.mapField("contentKind", AggregateState.EXTN, "content_kind_id", String.class);
	}

	@Override
	public ObjDocument doCreate() {
		return this.doCreate(this.dslContext().newRecord(Tables.OBJ_DOCUMENT));
	}

	@Override
	public ObjDocument doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		ObjDocumentRecord documentRecord = this.dslContext().fetchOne(Tables.OBJ_DOCUMENT,
				Tables.OBJ_DOCUMENT.OBJ_ID.eq(objId));
		if (documentRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(objId, documentRecord);
	}

}
