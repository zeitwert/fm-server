package io.dddrive.obj.model;

import java.time.OffsetDateTime;

import io.dddrive.oe.model.ObjUser;

public interface ObjPartTransition extends ObjPart<Obj> {

	Integer getSeqNr();

	Integer getUserId();

	ObjUser getUser();

	OffsetDateTime getTimestamp();

}
