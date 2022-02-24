
package io.zeitwert.ddd.obj.model;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.oe.model.ObjUser;

public interface Obj extends Aggregate {

	@Override
	ObjMeta getMeta();

	ObjUser getOwner();

	void setOwner(ObjUser owner);

}
