package io.zeitwert.ddd.aggregate.model.base;

/**
 * This interface defines the internal callbacks for an Aggregate
 * implementation.
 */
public interface AggregateSPI {

	/**
	 * Initialise the database records of an Aggregate with some basic fields (id,
	 * tenantId) after creation (internal, technical callback).
	 * 
	 * @param aggregateId aggregate id
	 * @param tenantId    tenant id
	 */
	public void doInit(Integer aggregateId, Integer tenantId);

	/**
	 * Do some work after create, f.ex. fire events, add transition etc.
	 */
	public void doAfterCreate();

	/**
	 * Do some work after load.
	 */
	public void doAfterLoad();

	/**
	 * Calculate all the derived fields, typically after a field change.
	 */
	public void calcAll();

	/**
	 * Calculate all the volatile derived fields, i.e. fields that are not saved to
	 * the database. This is triggered after loading the aggregate from the
	 * database.
	 */
	public void calcVolatile();

	/**
	 * Prepare for storage, f.ex. assign seqNr to parts.
	 */
	public void doBeforeStore();

	/**
	 * Store the database record(s) (of the Aggregate only). The Parts will be
	 * stored from the repository.
	 */
	public void doStore();

	/**
	 * Do some work after store.
	 */
	public void doAfterStore();

}
