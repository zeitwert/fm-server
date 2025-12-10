package io.dddrive.core.obj.model.base;

import java.time.OffsetDateTime;
import java.util.Set;

import io.dddrive.core.ddd.model.AggregateRepository;
import io.dddrive.core.ddd.model.base.AggregateRepositoryBase;
import io.dddrive.core.obj.model.Obj;
import io.dddrive.core.obj.model.ObjPartTransition;
import io.dddrive.core.obj.model.ObjRepository;

public abstract class ObjRepositoryBase<O extends Obj>
		extends AggregateRepositoryBase<O>
		implements ObjRepository<O> {

	private static final Set<String> NotLoggedProperties = Set.of("objTypeId", "closedByUser", "closedAt", "transitionList");

	protected ObjRepositoryBase(
			Class<? extends AggregateRepository<O>> repoIntfClass,
			Class<? extends Obj> intfClass,
			Class<? extends Obj> baseClass,
			String aggregateTypeId) {
		super(repoIntfClass, intfClass, baseClass, aggregateTypeId);
	}

	@Override
	public boolean doLogChange(String propertyName) {
		if (NotLoggedProperties.contains(propertyName)) {
			return false;
		}
		return super.doLogChange(propertyName);
	}

	@Override
	public void registerParts() {
		this.addPart(Obj.class, ObjPartTransition.class, ObjPartTransitionBase.class);
	}

	@Override
	public void delete(O obj, Object userId, OffsetDateTime timestamp) {
		obj.delete(userId, timestamp);
		this.store(obj, userId, timestamp);
	}

}
