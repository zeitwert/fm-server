
package fm.comunas.ddd.doc.model;

import java.time.OffsetDateTime;
import java.util.List;

import fm.comunas.ddd.aggregate.model.AggregateMeta;
import fm.comunas.ddd.oe.model.ObjUser;

public interface DocMeta extends AggregateMeta {

	OffsetDateTime getCreatedAt();

	ObjUser getCreatedByUser();

	OffsetDateTime getModifiedAt();

	ObjUser getModifiedByUser();

	List<DocPartTransition> getTransitionList();

}
