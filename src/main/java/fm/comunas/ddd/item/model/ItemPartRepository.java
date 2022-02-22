package fm.comunas.ddd.item.model;

import fm.comunas.ddd.aggregate.model.Aggregate;
import fm.comunas.ddd.part.model.PartRepository;

public interface ItemPartRepository<A extends Aggregate, P extends ItemPart<A>> extends PartRepository<A, P> {

}
