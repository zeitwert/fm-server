
package io.zeitwert.fm.doc.model.base;

import org.jooq.DSLContext;
import org.jooq.TableRecord;
import org.jooq.UpdatableRecord;
import org.jooq.exception.NoDataFoundException;

import io.zeitwert.ddd.aggregate.model.AggregateRepository;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.Doc;
import io.zeitwert.ddd.doc.model.base.DocRepositoryBase;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.doc.model.FMDoc;
import io.zeitwert.fm.doc.model.FMDocRepository;
import io.zeitwert.fm.doc.model.db.Tables;
import io.zeitwert.fm.doc.model.db.tables.records.DocRecord;
import io.zeitwert.fm.task.model.DocTaskRepository;

public abstract class FMDocRepositoryBase<D extends FMDoc, V extends TableRecord<?>> extends DocRepositoryBase<D, V>
		implements FMDocRepository<D, V> {

	private ObjNoteRepository noteRepository;
	private DocTaskRepository taskRepository;

	protected FMDocRepositoryBase(
			final Class<? extends AggregateRepository<D, V>> repoIntfClass,
			final Class<? extends Doc> intfClass,
			final Class<? extends Doc> baseClass,
			final String aggregateTypeId,
			final AppContext appContext,
			final DSLContext dslContext) {
		super(
				repoIntfClass,
				intfClass,
				baseClass,
				aggregateTypeId,
				appContext,
				dslContext);
	}

	@Override
	public ObjNoteRepository getNoteRepository() {
		if (this.noteRepository == null) {
			this.noteRepository = this.getAppContext().getBean(ObjNoteRepository.class);
		}
		return this.noteRepository;
	}

	@Override
	public DocTaskRepository getTaskRepository() {
		if (this.taskRepository == null) {
			this.taskRepository = this.getAppContext().getBean(DocTaskRepository.class);
		}
		return this.taskRepository;
	}

	protected D doCreate(UpdatableRecord<?> extnRecord) {
		return this.newAggregate(this.getDSLContext().newRecord(Tables.DOC), extnRecord);
	}

	protected D doLoad(Integer docId, UpdatableRecord<?> extnRecord) {
		DocRecord docRecord = this.getDSLContext().fetchOne(Tables.DOC, Tables.DOC.ID.eq(docId));
		if (docRecord == null || extnRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + docId + "]");
		}
		return this.newAggregate(docRecord, extnRecord);
	}

}
