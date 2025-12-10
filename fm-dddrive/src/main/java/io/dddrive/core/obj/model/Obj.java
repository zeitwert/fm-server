package io.dddrive.core.obj.model;

import java.time.OffsetDateTime;

import io.dddrive.core.ddd.model.Aggregate;

public interface Obj extends Aggregate {

	@Override
	ObjMeta getMeta();

	/**
	 * "Delete" the Object (i.e. set closed_by_user_id, closed_at)
	 */
	void delete(Object userId, OffsetDateTime timestamp);

}
