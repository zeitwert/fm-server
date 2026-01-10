package dddrive.app.obj.model

import dddrive.ddd.model.AggregateRepository

interface ObjRepository<O : Obj> : AggregateRepository<O> {

	/**
	 * Delete the Obj (i.e. set closed_at and store)
	 */
	fun close(obj: O)

}
