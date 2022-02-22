
package fm.comunas.ddd.obj.model;

import fm.comunas.ddd.aggregate.model.Aggregate;
import fm.comunas.ddd.oe.model.ObjUser;

public interface Obj extends Aggregate {

	@Override
	ObjMeta getMeta();

	ObjUser getOwner();

	void setOwner(ObjUser owner);

}
