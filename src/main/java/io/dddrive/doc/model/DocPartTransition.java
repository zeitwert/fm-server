package io.dddrive.doc.model;

import java.time.OffsetDateTime;

import io.dddrive.doc.model.enums.CodeCaseStage;
import io.dddrive.oe.model.ObjUser;

public interface DocPartTransition extends DocPart<Doc> {

	Integer getSeqNr();

	ObjUser getUser();

	OffsetDateTime getTimestamp();

	CodeCaseStage getOldCaseStage();

	CodeCaseStage getNewCaseStage();

}
