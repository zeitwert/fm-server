package io.dddrive.core.obj.model

import io.dddrive.core.ddd.model.AggregateRepository
import java.time.OffsetDateTime

interface ObjRepository<O : Obj> : AggregateRepository<O> {

	/**
	 * Delete the Obj (i.e. set closed_at and store)
	 */
	fun close(
		obj: O,
		userId: Any,
		timestamp: OffsetDateTime,
	)

}
