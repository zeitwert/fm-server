
package io.dddrive.obj.model;

import java.time.OffsetDateTime;
import java.util.List;

import io.dddrive.ddd.model.AggregateMeta;
import io.dddrive.oe.model.ObjUser;

public interface ObjMeta extends AggregateMeta {

	OffsetDateTime getClosedAt();

	ObjUser getClosedByUser();

	List<ObjPartTransition> getTransitionList();

}
