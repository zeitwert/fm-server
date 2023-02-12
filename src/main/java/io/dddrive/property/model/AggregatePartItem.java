package io.dddrive.property.model;

import io.dddrive.ddd.model.Aggregate;
import io.dddrive.ddd.model.Part;

public interface AggregatePartItem<A extends Aggregate> extends Part<A> {

	Integer getSeqNr();

	void setSeqNr(Integer seqNr);

	String getItemId();

	void setItemId(String item);

}
