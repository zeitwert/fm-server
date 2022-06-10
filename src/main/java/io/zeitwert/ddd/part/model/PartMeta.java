package io.zeitwert.ddd.part.model;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.entity.model.EntityMeta;
import io.zeitwert.ddd.part.model.base.PartStatus;

public interface PartMeta<A extends Aggregate> extends EntityMeta {

	A getAggregate();

	PartStatus getStatus();

}
