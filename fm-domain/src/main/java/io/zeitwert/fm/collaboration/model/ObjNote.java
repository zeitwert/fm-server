package io.zeitwert.fm.collaboration.model;

import io.dddrive.core.ddd.model.Aggregate;
import io.dddrive.core.obj.model.Obj;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType;

public interface ObjNote extends Obj {

	Object getRelatedToId();

	void setRelatedToId(Object id);

	Aggregate getRelated();

	CodeNoteType getNoteType();

	void setNoteType(CodeNoteType noteType);

	String getSubject();

	void setSubject(String subject);

	String getContent();

	void setContent(String subject);

	Boolean getIsPrivate();

	void setIsPrivate(Boolean isPrivate);

}
