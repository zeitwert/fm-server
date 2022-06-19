package io.zeitwert.ddd.part.model.base;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.property.model.enums.CodePartListType;

/**
 * This interface defines the internal callbacks for a Part implementation.
 */
public interface PartSPI<A extends Aggregate> {

	/**
	 * Get the parts persistence status.
	 * 
	 * @return persistence status
	 */
	PartStatus getStatus();

	/**
	 * Initialise the database records of a Part with some basic fields after
	 * creation (internal, technical callback).
	 * 
	 * @param partId       part id
	 * @param aggregate    aggregate
	 * @param parent       parent part
	 * @param partListType part list type
	 */
	void doInit(Integer partId, A aggregate, Part<?> parent, CodePartListType partListType);

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
	 * Store the part (insert/update/delete)
	 */
	void doStore();

	/**
	 * Do some work after store.
	 */
	public void doAfterStore();

}
