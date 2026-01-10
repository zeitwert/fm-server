package dddrive.ddd.model

/**
 * This interface defines the internal callbacks for a Part implementation.
 */
interface PartSPI<A : Aggregate> {

	/**
	 * Initialise a Part after creation (external, functional callback).
	 */
	fun doAfterCreate() {}

	fun delete()

}
