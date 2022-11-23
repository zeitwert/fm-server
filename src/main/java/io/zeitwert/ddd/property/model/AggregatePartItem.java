package io.zeitwert.ddd.property.model;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.part.model.Part;

public interface AggregatePartItem<A extends Aggregate> extends Part<A> {

	Integer getSeqNr();

	void setSeqNr(Integer seqNr);

	String getItemId();

	void setItemId(String item);

}
