package fm.comunas.ddd.part.model.base;

import fm.comunas.ddd.aggregate.model.Aggregate;
import fm.comunas.ddd.part.model.Part;
import fm.comunas.ddd.property.model.enums.CodePartListType;

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
	void afterCreate();

	/**
	 * Mark the part for deletion.
	 */
	void delete();

	/**
	 * Store the part (insert/update/delete)
	 */
	void store();

}
