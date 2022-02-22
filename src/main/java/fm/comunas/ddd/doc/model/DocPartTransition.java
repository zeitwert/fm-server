package fm.comunas.ddd.doc.model;

import java.time.OffsetDateTime;

import fm.comunas.ddd.doc.model.enums.CodeCaseStage;
import fm.comunas.ddd.oe.model.ObjUser;

public interface DocPartTransition extends DocPart<Doc> {

	Integer getSeqNr();

	ObjUser getUser();

	OffsetDateTime getModifiedAt();

	CodeCaseStage getOldCaseStage();

	CodeCaseStage getNewCaseStage();

	String getChanges();

}
