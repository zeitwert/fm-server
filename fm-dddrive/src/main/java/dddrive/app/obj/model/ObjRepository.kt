package dddrive.app.obj.model

import dddrive.ddd.model.AggregateRepository
import dddrive.hex.IncomingPort

interface ObjRepository<O : Obj> : AggregateRepository<O>, IncomingPort {

	/**
	 * Delete the Obj (i.e. set closed_at and store)
	 */
	fun close(obj: O)

}
