package io.zeitwert.fm.dms.model;

import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.fm.dms.model.db.tables.records.ObjDocumentVRecord;
import io.zeitwert.fm.dms.model.enums.CodeContentType;

public interface ObjDocumentRepository extends ObjRepository<ObjDocument, ObjDocumentVRecord> {

	byte[] getContent(ObjDocument document);

	CodeContentType getContentType(ObjDocument document);

	void storeContent(SessionInfo sessionInfo, ObjDocument document, CodeContentType contentType, byte[] content);

}
