package io.dddrive.core.obj.model;

import io.dddrive.core.ddd.model.AggregateMeta;
import io.dddrive.core.oe.model.ObjUser;

import java.time.OffsetDateTime;
import java.util.List;

public interface ObjMeta extends AggregateMeta {

	String getObjTypeId();

	OffsetDateTime getClosedAt();

	ObjUser getClosedByUser();

	List<ObjPartTransition> getTransitionList();

}
