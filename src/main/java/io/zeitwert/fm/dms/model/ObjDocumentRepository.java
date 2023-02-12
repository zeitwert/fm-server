package io.zeitwert.fm.dms.model;

import io.dddrive.app.model.RequestContext;
import io.dddrive.obj.model.ObjRepository;
import io.zeitwert.fm.dms.model.db.tables.records.ObjDocumentVRecord;
import io.zeitwert.fm.dms.model.enums.CodeContentType;

public interface ObjDocumentRepository extends ObjRepository<ObjDocument, ObjDocumentVRecord> {

	byte[] getContent(ObjDocument document);

	CodeContentType getContentType(ObjDocument document);

	void storeContent(RequestContext requestCtx, ObjDocument document, CodeContentType contentType, byte[] content);

}
