package io.zeitwert.ddd.item.model;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.part.model.PartRepository;

public interface ItemPartRepository<A extends Aggregate, P extends ItemPart<A>> extends PartRepository<A, P> {

}
