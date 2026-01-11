package dddrive.ddd.model

interface PartRepository<A : Aggregate, P : Part<A>> {

	/**
	 * Do we fire property change events for this property?
	 */
	fun doLogChange(property: String): Boolean

	/**
	 * Create a new Part instance within the given aggregate.
	 *
	 * @param aggregate the aggregate
	 * @param property  the (collection) property that contains the part
	 * @param partId    the part id (if loaded from persistence), or null if the id of a new part should be generated
	 */
	fun create(
		aggregate: A,
		property: dddrive.property.model.Property<*>,
		partId: Int?,
	): P

}
