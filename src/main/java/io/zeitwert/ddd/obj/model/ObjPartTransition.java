package io.zeitwert.ddd.obj.model;

import io.zeitwert.ddd.oe.model.ObjUser;

import java.time.OffsetDateTime;

public interface ObjPartTransition extends ObjPart<Obj> {

	Integer getSeqNr();

	Integer getUserId();

	ObjUser getUser();

	OffsetDateTime getTimestamp();

}
