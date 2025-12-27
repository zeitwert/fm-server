package dddrive.app.obj.model

import dddrive.ddd.core.model.AggregateRepository
import java.time.OffsetDateTime

interface ObjRepository<O : Obj> : dddrive.ddd.core.model.AggregateRepository<O> {

	/**
	 * Delete the Obj (i.e. set closed_at and store)
	 */
	fun close(
		obj: O,
		userId: Any,
		timestamp: OffsetDateTime,
	)

}
