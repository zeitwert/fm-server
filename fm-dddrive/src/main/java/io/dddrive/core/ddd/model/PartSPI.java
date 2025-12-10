package io.dddrive.core.ddd.model;

/**
 * This interface defines the internal callbacks for a Part implementation.
 */
public interface PartSPI<A extends Aggregate> {

	/**
	 * Initialise a Part after creation (external, functional callback).
	 */
	default void doAfterCreate() {}

	/**
	 * Initialise a Part after load (external, functional callback).
	 */
	void doAfterLoad();

	void delete();

}
