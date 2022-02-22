package fm.comunas.ddd.part.model;

import fm.comunas.ddd.aggregate.model.Aggregate;
import fm.comunas.ddd.entity.model.EntityMeta;

public interface PartMeta<A extends Aggregate> extends EntityMeta {

	A getAggregate();

}
