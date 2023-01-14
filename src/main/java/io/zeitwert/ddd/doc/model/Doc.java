
package io.zeitwert.ddd.doc.model;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStage;
import io.zeitwert.ddd.oe.model.ObjUser;

public interface Doc extends Aggregate {

	@Override
	DocMeta getMeta();

	boolean isInWork();

	CodeCaseStage getCaseStage();

	void setCaseStage(CodeCaseStage caseStage);

	ObjUser getAssignee();

	void setAssignee(ObjUser assignee);

}
