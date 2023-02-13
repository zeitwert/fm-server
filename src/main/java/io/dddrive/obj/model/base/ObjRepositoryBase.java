
package io.dddrive.obj.model.base;

import javax.annotation.PostConstruct;

import io.dddrive.ddd.model.AggregateRepository;
import io.dddrive.ddd.model.base.AggregateRepositoryBase;
import io.dddrive.obj.model.Obj;
import io.dddrive.obj.model.ObjRepository;

public abstract class ObjRepositoryBase<O extends Obj, V extends Object>
		extends AggregateRepositoryBase<O, V>
		implements ObjRepository<O, V> {

	protected ObjRepositoryBase(
			Class<? extends AggregateRepository<O, V>> repoIntfClass,
			Class<? extends Obj> intfClass,
			Class<? extends Obj> baseClass,
			String aggregateTypeId) {
		super(repoIntfClass, intfClass, baseClass, aggregateTypeId);
	}

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		this.addPartRepository(this.getTransitionRepository());
		this.addPartRepository(this.getItemRepository());
	}

	@Override
	public void delete(O obj) {
		obj.delete();
		this.store(obj);
	}

}
