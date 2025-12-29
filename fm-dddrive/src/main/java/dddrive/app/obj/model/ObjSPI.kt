package dddrive.app.obj.model

import dddrive.app.ddd.model.AggregateSPI
import dddrive.app.ddd.model.SessionContext

interface ObjSPI : AggregateSPI {

	fun doBeforeClose(sessionContext: SessionContext)

	fun doClose(sessionContext: SessionContext)

	fun doAfterClose(sessionContext: SessionContext)

}
