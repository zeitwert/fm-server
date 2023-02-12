package io.dddrive.ddd.model;

/**
 * A Part is an Entity that belongs to an Aggregate.
 */
public interface Part<A extends Aggregate> {

	PartMeta<A> getMeta();

	Integer getId();

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

}
