package dddrive.ddd.core.model

import dddrive.ddd.property.model.EntityWithPropertiesSPI
import dddrive.ddd.property.model.PropertyChangeListener

/**
 * This interface defines the internal callbacks for an Aggregate
 * implementation.
 */
interface AggregateSPI : EntityWithPropertiesSPI {

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

	fun addPropertyChangeListener(listener: PropertyChangeListener)

	fun removePropertyChangeListener(listener: PropertyChangeListener)

	fun doBeforeStore()

	fun doAfterStore()

}
