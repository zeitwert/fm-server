
package io.zeitwert.ddd.obj.model;

import io.zeitwert.ddd.aggregate.model.Aggregate;

public interface Obj extends Aggregate {

	@Override
	ObjMeta getMeta();

}
