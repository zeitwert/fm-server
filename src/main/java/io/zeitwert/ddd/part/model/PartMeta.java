package io.zeitwert.ddd.part.model;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.entity.model.EntityMeta;

public interface PartMeta<A extends Aggregate> extends EntityMeta {

	A getAggregate();

}
