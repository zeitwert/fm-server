package io.zeitwert.ddd.item.model.base;

import org.jooq.DSLContext;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.item.model.ItemPart;
import io.zeitwert.ddd.item.model.ItemPartRepository;
import io.zeitwert.ddd.part.model.base.PartRepositoryBase;

public abstract class ItemPartRepositoryBase<A extends Aggregate, P extends ItemPart<A>>
		extends PartRepositoryBase<A, P>
		implements ItemPartRepository<A, P> {

	//@formatter:off
	protected ItemPartRepositoryBase(
		final Class<? extends A> aggregateIntfClass,
		final Class<? extends ItemPart<A>> intfClass,
		final Class<? extends ItemPart<A>> baseClass,
		final String partTypeId,
		final AppContext appContext,
		final DSLContext dslContext
	) {
		super(aggregateIntfClass, intfClass, baseClass, partTypeId, appContext, dslContext);
	}
	//@formatter:on

}
