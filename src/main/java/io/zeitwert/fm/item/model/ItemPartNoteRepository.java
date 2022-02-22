
package io.zeitwert.fm.item.model;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.item.model.ItemPartRepository;

public interface ItemPartNoteRepository<A extends Aggregate> extends ItemPartRepository<A, ItemPartNote<A>> {

}
