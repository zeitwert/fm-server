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

	void setSeqNr(Integer seqNr);

}
