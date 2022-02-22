
package io.zeitwert.ddd.doc.model;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.doc.model.enums.CodeCaseStage;
import io.zeitwert.ddd.oe.model.ObjUser;

public interface Doc extends Aggregate {

	@Override
	DocMeta getMeta();

	ObjUser getOwner();

	void setOwner(ObjUser owner);

	CodeCaseStage getCaseStage();

	void setCaseStage(CodeCaseStage caseStage);

	ObjUser getAssignee();

	void setAssignee(ObjUser assignee);

}
