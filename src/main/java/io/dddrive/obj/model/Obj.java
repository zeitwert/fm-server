
package io.dddrive.obj.model;

import io.dddrive.ddd.model.Aggregate;

public interface Obj extends Aggregate {

	@Override
	ObjMeta getMeta();

	/**
	 * "Delete" the Object (i.e. set closed_by_user_id, closed_at)
	 */
	void delete();

}
