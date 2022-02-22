package fm.comunas.ddd.part.model;

import fm.comunas.ddd.aggregate.model.Aggregate;

/**
 * A Part is an Entity that belongs to an Aggregate.
 */
public interface Part<A extends Aggregate> {

	PartMeta<A> getMeta();

	Integer getId();

	Integer getParentPartId();

	String getPartListTypeId();

	Integer getSeqNr();

	void setSeqNr(Integer seqNr);

}
