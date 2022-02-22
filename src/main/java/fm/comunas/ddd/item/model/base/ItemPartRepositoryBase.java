package fm.comunas.ddd.item.model.base;

import org.jooq.DSLContext;

import fm.comunas.ddd.aggregate.model.Aggregate;
import fm.comunas.ddd.app.service.api.AppContext;
import fm.comunas.ddd.item.model.ItemPart;
import fm.comunas.ddd.item.model.ItemPartRepository;
import fm.comunas.ddd.part.model.base.PartRepositoryBase;

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
