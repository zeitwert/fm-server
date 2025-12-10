package io.dddrive.core.doc.model.base;

import java.util.Set;

import io.dddrive.core.ddd.model.AggregateRepository;
import io.dddrive.core.ddd.model.base.AggregateRepositoryBase;
import io.dddrive.core.doc.model.Doc;
import io.dddrive.core.doc.model.DocPartTransition;
import io.dddrive.core.doc.model.DocRepository;

public abstract class DocRepositoryBase<D extends Doc>
		extends AggregateRepositoryBase<D>
		implements DocRepository<D> {

	private static final Set<String> NotLoggedProperties = Set.of("docTypeId", "caseDef", "isInWork", "transitionList");

	protected DocRepositoryBase(
			Class<? extends AggregateRepository<D>> repoIntfClass,
			Class<? extends Doc> intfClass,
			Class<? extends Doc> baseClass,
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
		this.addPart(Doc.class, DocPartTransition.class, DocPartTransitionBase.class);
	}

}
