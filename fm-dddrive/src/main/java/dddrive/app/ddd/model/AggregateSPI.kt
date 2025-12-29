package dddrive.app.ddd.model

import dddrive.ddd.core.model.AggregateSPI

interface AggregateSPI : AggregateSPI {

	/**
	 * Initialize aggregate with some basic fields after creation.
	 */
	fun doAfterCreate(sessionContext: SessionContext)

	/**
	 * Prepare for storage, f.ex. assign seqNr to parts.
	 */
	fun doBeforeStore(sessionContext: SessionContext)

}
