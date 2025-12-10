package io.zeitwert.fm.doc.model.base;

import java.util.List;

import org.jooq.TableRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import io.crnk.core.queryspec.QuerySpec;
import io.dddrive.ddd.model.AggregateRepository;
import io.dddrive.doc.model.Doc;
import io.dddrive.jooq.ddd.AggregateState;
import io.dddrive.jooq.doc.JooqDocExtnRepositoryBase;
import io.zeitwert.fm.app.model.RequestContextFM;
// TODO-MIGRATION: Collaboration - uncomment after Collaboration mixin is restored
// import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.ddd.model.base.AggregateFindMixin;
import io.zeitwert.fm.doc.model.FMDocRepository;
import io.zeitwert.fm.task.model.DocTaskRepository;

public abstract class FMDocRepositoryBase<D extends Doc, V extends TableRecord<?>>
		extends JooqDocExtnRepositoryBase<D, V>
		implements FMDocRepository<D, V>, DocPersistenceProviderMixin<D>, AggregateFindMixin<V> {

	// TODO-MIGRATION: Collaboration - uncomment after Collaboration mixin is restored
	// private ObjNoteRepository noteRepository;
	private DocTaskRepository taskRepository;

	public FMDocRepositoryBase(
			Class<? extends AggregateRepository<D, V>> repoIntfClass,
			Class<? extends Doc> intfClass,
			Class<? extends Doc> baseClass,
			String aggregateTypeId) {
		super(repoIntfClass, intfClass, baseClass, aggregateTypeId);
	}

	// TODO-MIGRATION: Collaboration - uncomment after Collaboration mixin is restored
	// @Autowired
	// @Lazy
	// void setNoteRepository(ObjNoteRepository noteRepository) {
	// 	this.noteRepository = noteRepository;
	// }

	@Autowired
	@Lazy
	void setTaskRepository(DocTaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	// TODO-MIGRATION: Collaboration - uncomment after Collaboration mixin is restored
	// @Override
	// public ObjNoteRepository getNoteRepository() {
	// 	return this.noteRepository;
	// }

	@Override
	public DocTaskRepository getTaskRepository() {
		return this.taskRepository;
	}

	@Override
	public void mapProperties() {
		super.mapProperties();
		this.mapField("accountId", AggregateState.BASE, "account_id", Integer.class);
		this.mapField("extnAccountId", AggregateState.EXTN, "account_id", Integer.class);
	}

	@Override
	public boolean hasAccount() {
		return true;
	}

	@Override
	public final List<V> find(QuerySpec querySpec) {
		return this.doFind(this.queryWithFilter(querySpec, (RequestContextFM) this.getAppContext().getRequestContext()));
	}

	@Override
	public Integer getId(String key) {
		return null;
	}

}
