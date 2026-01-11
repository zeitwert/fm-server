package dddrive.ddd.model

/**
 * This interface defines the internal callbacks for an Aggregate
 * implementation.
 */
interface AggregateSPI {

	/**
	 * Generate new part id.
	 *
	 * @param partClass part class
	 * @return new part id
	 */
	fun <P : Part<*>> nextPartId(partClass: Class<P>): Int

	fun doAfterCreate()

	fun beginLoad()

	fun endLoad()

	fun doAfterLoad()

	fun addPropertyChangeListener(listener: dddrive.property.model.PropertyChangeListener)

	fun removePropertyChangeListener(listener: dddrive.property.model.PropertyChangeListener)

	fun doBeforeStore()

	fun doAfterStore()

	fun fireFieldChange(
		op: String,
		path: String,
		value: Any?,
		oldValue: Any?,
		isInCalc: Boolean,
	)

}
