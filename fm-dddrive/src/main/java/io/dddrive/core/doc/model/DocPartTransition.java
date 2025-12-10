package io.dddrive.core.doc.model;

import java.time.OffsetDateTime;

import io.dddrive.core.doc.model.enums.CodeCaseStage;
import io.dddrive.core.oe.model.ObjUser;

public interface DocPartTransition extends DocPart<Doc> {

	Integer getSeqNr();

	ObjUser getUser();

	OffsetDateTime getTimestamp();

	CodeCaseStage getOldCaseStage();

	CodeCaseStage getNewCaseStage();

}
