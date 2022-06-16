package io.zeitwert.ddd.collaboration.model;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.collaboration.model.enums.CodeNoteType;
import io.zeitwert.ddd.obj.model.Obj;

public interface ObjNote extends Obj {

	Integer getRelatedToId();

	void setRelatedToId(Integer id);

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
