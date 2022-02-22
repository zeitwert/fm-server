
package fm.comunas.ddd.obj.model;

import java.time.OffsetDateTime;
import java.util.List;

import fm.comunas.ddd.aggregate.model.AggregateMeta;
import fm.comunas.ddd.oe.model.ObjUser;

public interface ObjMeta extends AggregateMeta {

	OffsetDateTime getCreatedAt();

	ObjUser getCreatedByUser();

	OffsetDateTime getModifiedAt();

	ObjUser getModifiedByUser();

	List<ObjPartTransition> getTransitionList();

}
