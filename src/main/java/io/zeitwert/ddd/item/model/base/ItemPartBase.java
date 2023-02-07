
package io.zeitwert.ddd.item.model.base;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.item.model.ItemPart;
import io.zeitwert.ddd.part.model.PartRepository;
import io.zeitwert.ddd.part.model.base.PartBase;
import io.zeitwert.jooq.persistence.PartState;

public abstract class ItemPartBase<A extends Aggregate> extends PartBase<A> implements ItemPart<A> {

	protected ItemPartBase(PartRepository<A, ?> repository, A obj, PartState state) {
		super(repository, obj, state);
	}

}
