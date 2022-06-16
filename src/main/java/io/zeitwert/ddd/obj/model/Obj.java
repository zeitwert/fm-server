
package io.zeitwert.ddd.obj.model;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.collaboration.model.ItemWithNotes;

public interface Obj extends Aggregate, ItemWithNotes {

	@Override
	ObjMeta getMeta();

}
