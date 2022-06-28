
package io.zeitwert.ddd.obj.model;

import java.time.OffsetDateTime;
import java.util.List;

import io.zeitwert.ddd.aggregate.model.AggregateMeta;
import io.zeitwert.ddd.oe.model.ObjUser;

public interface ObjMeta extends AggregateMeta {

	OffsetDateTime getClosedAt();

	ObjUser getClosedByUser();

	List<ObjPartTransition> getTransitionList();

}
