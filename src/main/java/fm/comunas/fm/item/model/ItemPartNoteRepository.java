
package fm.comunas.fm.item.model;

import fm.comunas.ddd.aggregate.model.Aggregate;
import fm.comunas.ddd.item.model.ItemPartRepository;

public interface ItemPartNoteRepository<A extends Aggregate> extends ItemPartRepository<A, ItemPartNote<A>> {

}
