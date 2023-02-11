
package io.zeitwert.ddd.obj.model.base;

import javax.annotation.PostConstruct;

import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.aggregate.model.base.AggregateRepositoryBase;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.obj.model.ObjRepository;

public abstract class ObjRepositoryBase<O extends Obj, V extends Object>
		extends AggregateRepositoryBase<O, V>
		implements ObjRepository<O, V> {

	protected ObjRepositoryBase(
			Class<? extends AggregateRepository<O, V>> repoIntfClass,
			Class<? extends Obj> intfClass,
			Class<? extends Obj> baseClass,
			String aggregateTypeId,
			AppContext appContext) {
		super(repoIntfClass, intfClass, baseClass, aggregateTypeId, appContext);
	}

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		this.addPartRepository(ObjRepository.getTransitionRepository());
		this.addPartRepository(ObjRepository.getItemRepository());
	}

	@Override
	public void delete(O obj) {
		obj.delete();
		this.store(obj);
	}

}
