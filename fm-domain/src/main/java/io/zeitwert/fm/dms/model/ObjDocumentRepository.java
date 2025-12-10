package io.zeitwert.fm.dms.model;

import io.dddrive.core.obj.model.ObjRepository;
import io.zeitwert.fm.dms.model.enums.CodeContentType;

public interface ObjDocumentRepository extends ObjRepository<ObjDocument> {

	byte[] getContent(ObjDocument document);

	CodeContentType getContentType(ObjDocument document);

	void storeContent(ObjDocument document, CodeContentType contentType, byte[] content);

}
