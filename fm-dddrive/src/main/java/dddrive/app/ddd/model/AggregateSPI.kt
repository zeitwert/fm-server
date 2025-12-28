package dddrive.app.ddd.model

interface AggregateSPI {

	/**
	 * Initialize aggregate with some basic fields after creation.
	 */
	fun doAfterCreate(sessionContext: SessionContext)

	/**
	 * Prepare for storage, f.ex. assign seqNr to parts.
	 */
	fun doBeforeStore(sessionContext: SessionContext)

}
