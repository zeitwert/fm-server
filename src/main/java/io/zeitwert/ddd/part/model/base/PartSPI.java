package io.zeitwert.ddd.part.model.base;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.part.model.PartPersistenceStatus;

/**
 * This interface defines the internal callbacks for a Part implementation.
 */
public interface PartSPI<A extends Aggregate> {

	/**
	 * Get the persistence provider specific part state.
	 *
	 * @return part state
	 */
	Object getPartState();

	/**
	 * Get the parts persistence status.
	 *
	 * @return persistence status
	 */
	PartPersistenceStatus getPersistenceStatus();

	/**
	 * Initialise a Part after creation (external, functional callback).
	 */
	void doAfterCreate();

	/**
	 * Assign Parts to Part lists after Load
	 */
	void doAssignParts();

	/**
	 * Do some work after load.
	 */
	public void doAfterLoad();

	/**
	 * Mark the part for deletion.
	 */
	void delete();

	/**
	 * Prepare for storage, f.ex. assign modified_at, modified_by_user_id.
	 */
	public void doBeforeStore();

	/**
	 * Do some work after store.
	 */
	public void doAfterStore();

}
