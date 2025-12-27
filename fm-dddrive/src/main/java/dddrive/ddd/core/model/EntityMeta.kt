package dddrive.ddd.core.model

interface EntityMeta {

	val isNew: Boolean

	val isFrozen: Boolean

	val isInLoad: Boolean

	fun isInCalc(): Boolean

	fun isCalcEnabled(): Boolean

	fun disableCalc()

	fun enableCalc()

	/**
	 * Calculate all the derived fields, typically after a field change.
	 */
	fun calcAll()

	/**
	 * Calculate all the volatile derived fields, i.e. fields that are not saved to
	 * the database. This is triggered after loading the aggregate from the
	 * database.
	 */
	fun calcVolatile()

}
