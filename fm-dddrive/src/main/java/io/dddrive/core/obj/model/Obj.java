package io.dddrive.core.obj.model;

import io.dddrive.core.ddd.model.Aggregate;

import java.time.OffsetDateTime;

public interface Obj extends Aggregate {

	@Override
	ObjMeta getMeta();

	/**
	 * "Delete" the Object (i.e. set closed_by_user_id, closed_at)
	 */
	void delete(Object userId, OffsetDateTime timestamp);

}
