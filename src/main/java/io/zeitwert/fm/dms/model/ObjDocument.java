package io.zeitwert.fm.dms.model;

import io.zeitwert.fm.dms.model.enums.CodeContentKind;
import io.zeitwert.fm.dms.model.enums.CodeContentType;
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory;
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind;
import io.zeitwert.fm.obj.model.FMObj;

public interface ObjDocument extends FMObj {

	String getName();

	void setName(String name);

	CodeContentKind getContentKind();

	void setContentKind(CodeContentKind contentKind);

	CodeDocumentKind getDocumentKind();

	void setDocumentKind(CodeDocumentKind documentKind);

	CodeDocumentCategory getDocumentCategory();

	void setDocumentCategory(CodeDocumentCategory documentCategory);

	Integer getTemplateDocumentId();

	void setTemplateDocumentId(Integer id);

	ObjDocument getTemplateDocument();

	CodeContentType getContentType();

	byte[] getContent();

	void storeContent(CodeContentType contentType, byte[] content);

}
