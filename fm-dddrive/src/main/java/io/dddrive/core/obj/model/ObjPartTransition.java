package io.dddrive.core.obj.model;

import java.time.OffsetDateTime;

import io.dddrive.core.oe.model.ObjUser;

public interface ObjPartTransition extends ObjPart<Obj> {

	ObjUser getUser();

	OffsetDateTime getTimestamp();

}
