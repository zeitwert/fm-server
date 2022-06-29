package io.zeitwert.ddd.doc.model;

import java.time.OffsetDateTime;

import io.zeitwert.ddd.doc.model.enums.CodeCaseStage;
import io.zeitwert.ddd.oe.model.ObjUser;

public interface DocPartTransition extends DocPart<Doc> {

	Integer getSeqNr();

	ObjUser getUser();

	OffsetDateTime getTimestamp();

	CodeCaseStage getOldCaseStage();

	CodeCaseStage getNewCaseStage();

	String getChanges();

}
