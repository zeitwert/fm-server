package io.zeitwert.ddd.item.model;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.part.model.Part;

public interface ItemPart<A extends Aggregate> extends Part<A> {

}
