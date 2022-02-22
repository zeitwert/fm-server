
package io.zeitwert.ddd.doc.model;

import java.time.OffsetDateTime;
import java.util.List;

import io.zeitwert.ddd.aggregate.model.AggregateMeta;
import io.zeitwert.ddd.oe.model.ObjUser;

public interface DocMeta extends AggregateMeta {

	OffsetDateTime getCreatedAt();

	ObjUser getCreatedByUser();

	OffsetDateTime getModifiedAt();

	ObjUser getModifiedByUser();

	List<DocPartTransition> getTransitionList();

}
