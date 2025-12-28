package dddrive.ddd.core.model

/**
 * This class defines the internal callbacks for an AggregateRepository
 * implementation.
 */
interface AggregateRepositorySPI<A : Aggregate> {

	/**
	 * Register the parts of the aggregate
	 */
	fun registerParts()

	/**
	 * Get the PersistenceProvider for this repository
	 *
	 * @return AggregatePersistenceProvider
	 */
	val persistenceProvider: AggregatePersistenceProvider<A>

	/**
	 * Create a new aggregate instance. Concrete repositories must override this to directly
	 * instantiate their Impl class.
	 */
	fun createAggregate(isNew: Boolean): A

	/**
	 * Do some work after create, f.ex. fire events
	 *
	 * @param aggregate aggregate
	 */
	fun doAfterCreate(aggregate: A)

	/**
	 * Do some work after load, f.ex. fire events
	 *
	 * @param aggregate aggregate
	 */
	fun doAfterLoad(aggregate: A)

	/**
	 * Do some work before store, f.ex. make sure certain structures are in place
	 *
	 * @param aggregate aggregate
	 */
	fun doBeforeStore(aggregate: A)

	/**
	 * Do some work after store, f.ex. fire events
	 *
	 * @param aggregate aggregate
	 */
	fun doAfterStore(aggregate: A)

}
