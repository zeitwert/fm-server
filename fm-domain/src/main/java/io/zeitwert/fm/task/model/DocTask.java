package io.zeitwert.fm.task.model;

import java.time.OffsetDateTime;

import io.dddrive.core.ddd.model.Aggregate;
import io.dddrive.core.doc.model.Doc;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.task.model.enums.CodeTaskPriority;

public interface DocTask extends Doc {

	Integer getRelatedToId();

	void setRelatedToId(Integer id);

	Aggregate getRelatedTo();

	ObjAccount getAccount();

	String getSubject();

	void setSubject(String subject);

	String getContent();

	void setContent(String content);

	Boolean getIsPrivate();

	void setIsPrivate(Boolean isPrivate);

	CodeTaskPriority getPriority();

	void setPriority(CodeTaskPriority priority);

	OffsetDateTime getDueAt();

	void setDueAt(OffsetDateTime dueAt);

	OffsetDateTime getRemindAt();

	void setRemindAt(OffsetDateTime remindAt);

}
