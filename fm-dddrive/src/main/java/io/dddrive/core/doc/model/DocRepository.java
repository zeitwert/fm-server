package io.dddrive.core.doc.model;

import io.dddrive.core.ddd.model.AggregateRepository;

public interface DocRepository<D extends Doc> extends AggregateRepository<D> {

	//DocPartTransitionRepository getTransitionRepository();

	//DocPartItemRepository getItemRepository();

}
