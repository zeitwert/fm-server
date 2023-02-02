package io.zeitwert.ddd.item.model.base;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.item.model.ItemPart;
import io.zeitwert.ddd.item.model.ItemPartRepository;
import io.zeitwert.ddd.part.model.base.PartRepositoryBase;

public abstract class ItemPartRepositoryBase<A extends Aggregate, P extends ItemPart<A>>
		extends PartRepositoryBase<A, P>
		implements ItemPartRepository<A, P> {

	protected ItemPartRepositoryBase(
			Class<? extends A> aggregateIntfClass,
			Class<? extends ItemPart<A>> intfClass,
			Class<? extends ItemPart<A>> baseClass,
			String partTypeId,
			AppContext appContext) {
		super(aggregateIntfClass, intfClass, baseClass, partTypeId, appContext);
	}

}
