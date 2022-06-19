package io.zeitwert.ddd.part.model;

import io.zeitwert.ddd.aggregate.model.Aggregate;

/**
 * A Part is an Entity that belongs to an Aggregate.
 */
public interface Part<A extends Aggregate> {

	PartRepository<A, ?> getRepository();

	PartMeta<A> getMeta();

	Integer getId();

	Integer getParentPartId();

	String getPartListTypeId();

	Integer getSeqNr();

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
