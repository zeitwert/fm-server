
package fm.comunas.ddd.doc.model;

import fm.comunas.ddd.aggregate.model.Aggregate;
import fm.comunas.ddd.doc.model.enums.CodeCaseStage;
import fm.comunas.ddd.oe.model.ObjUser;

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
