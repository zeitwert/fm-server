
package io.dddrive.doc.model;

import io.dddrive.ddd.model.Aggregate;
import io.dddrive.doc.model.enums.CodeCaseStage;
import io.dddrive.oe.model.ObjUser;

public interface Doc extends Aggregate {

	@Override
	DocMeta getMeta();

	boolean isInWork();

	CodeCaseStage getCaseStage();

	void setCaseStage(CodeCaseStage caseStage);

	ObjUser getAssignee();

	void setAssignee(ObjUser assignee);

}
