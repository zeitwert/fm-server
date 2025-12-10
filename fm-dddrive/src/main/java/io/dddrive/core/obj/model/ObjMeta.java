
package io.dddrive.core.obj.model;

import java.time.OffsetDateTime;
import java.util.List;

import io.dddrive.core.ddd.model.AggregateMeta;
import io.dddrive.core.oe.model.ObjUser;

public interface ObjMeta extends AggregateMeta {

	OffsetDateTime getClosedAt();

	ObjUser getClosedByUser();

	List<ObjPartTransition> getTransitionList();

}
