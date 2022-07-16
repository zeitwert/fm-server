
package io.zeitwert.ddd.obj.model;

import io.zeitwert.ddd.aggregate.model.Aggregate;

public interface Obj extends Aggregate {

	@Override
	ObjMeta getMeta();

	/**
	 * "Delete" the Object (i.e. set closed_by_user_id, closed_at)
	 */
	void delete();

}
