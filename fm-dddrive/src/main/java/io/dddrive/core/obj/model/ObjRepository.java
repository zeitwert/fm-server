package io.dddrive.core.obj.model;

import java.time.OffsetDateTime;

import io.dddrive.core.ddd.model.AggregateRepository;

public interface ObjRepository<O extends Obj> extends AggregateRepository<O> {

	//ObjPartTransitionRepository getTransitionRepository();

	//ObjPartItemRepository getItemRepository();

	/**
	 * Delete the Obj (i.e. set closed_at and store)
	 */
	void delete(O obj, Object userId, OffsetDateTime timestamp);

}
