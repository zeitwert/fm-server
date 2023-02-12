package io.zeitwert.fm.dms.model;

import io.dddrive.obj.model.Obj;
import io.zeitwert.fm.account.model.ItemWithAccount;
import io.zeitwert.fm.collaboration.model.ItemWithNotes;
import io.zeitwert.fm.dms.model.enums.CodeContentKind;
import io.zeitwert.fm.dms.model.enums.CodeContentType;
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategory;
import io.zeitwert.fm.dms.model.enums.CodeDocumentKind;
import io.zeitwert.fm.task.model.ItemWithTasks;

public interface ObjDocument extends Obj, ItemWithAccount, ItemWithNotes, ItemWithTasks {

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
