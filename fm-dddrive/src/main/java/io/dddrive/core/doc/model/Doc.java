package io.dddrive.core.doc.model;

import java.time.OffsetDateTime;

import io.dddrive.core.ddd.model.Aggregate;
import io.dddrive.core.doc.model.enums.CodeCaseDef;
import io.dddrive.core.doc.model.enums.CodeCaseStage;
import io.dddrive.core.oe.model.ObjUser;

public interface Doc extends Aggregate {

	@Override
	DocMeta getMeta();

	void setCaseDef(CodeCaseDef caseDef);

	void setCaseStage(CodeCaseStage caseStage, Object userId, OffsetDateTime timestamp);

	ObjUser getAssignee();

	void setAssignee(ObjUser assignee);

}
