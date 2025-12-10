package io.zeitwert.fm.dms.model;

import io.dddrive.app.model.RequestContext;
import io.zeitwert.fm.account.service.api.ObjAccountCache;
import io.zeitwert.fm.dms.model.db.tables.records.ObjDocumentVRecord;
import io.zeitwert.fm.dms.model.enums.CodeContentType;
import io.zeitwert.fm.obj.model.FMObjRepository;

public interface ObjDocumentRepository extends FMObjRepository<ObjDocument, ObjDocumentVRecord> {

	ObjAccountCache getAccountCache();

	byte[] getContent(ObjDocument document);

	CodeContentType getContentType(ObjDocument document);

	void storeContent(RequestContext requestCtx, ObjDocument document, CodeContentType contentType, byte[] content);

}
