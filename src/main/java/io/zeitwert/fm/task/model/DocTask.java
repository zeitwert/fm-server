
package io.zeitwert.fm.task.model;

import java.time.OffsetDateTime;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.doc.model.FMDoc;
import io.zeitwert.fm.task.model.enums.CodeTaskPriority;

public interface DocTask extends FMDoc {

	Integer getRelatedToId();

	void setRelatedToId(Integer id);

	Aggregate getRelatedTo();

	Integer getAccountId();

	void setAccountId(Integer id);

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
