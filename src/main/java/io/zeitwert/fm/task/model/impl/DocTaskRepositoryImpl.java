
package io.zeitwert.fm.task.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.DocPartTransitionRepository;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.doc.model.base.FMDocRepositoryBase;
import io.zeitwert.fm.obj.model.ObjVRepository;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.DocTaskRepository;
import io.zeitwert.fm.task.model.base.DocTaskBase;
import io.zeitwert.fm.task.model.db.Tables;
import io.zeitwert.fm.task.model.db.tables.records.DocTaskRecord;
import io.zeitwert.fm.task.model.db.tables.records.DocTaskVRecord;

@Component("docTaskRepository")
public class DocTaskRepositoryImpl extends FMDocRepositoryBase<DocTask, DocTaskVRecord>
		implements DocTaskRepository {

	private static final String AGGREGATE_TYPE = "doc_task";

	private final ObjVRepository objVRepository;

	//@formatter:off
	protected DocTaskRepositoryImpl(
		final AppContext appContext,
		final DSLContext dslContext,
		final DocPartTransitionRepository transitionRepository,
		final ObjNoteRepository noteRepository,
		final ObjVRepository objVRepository
	) {
		super(
			DocTaskRepository.class,
			DocTask.class,
			DocTaskBase.class,
			AGGREGATE_TYPE,
			appContext,
			dslContext,
			transitionRepository,
			noteRepository
		);
		this.objVRepository = objVRepository;
	}
	//@formatter:on

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
	}

	@Override
	public ObjVRepository getObjRepository() {
		return this.objVRepository;
	}

	@Override
	public DocTask doCreate() {
		return this.doCreate(this.getDSLContext().newRecord(Tables.DOC_TASK));
	}

	@Override
	public DocTask doLoad(Integer docId) {
		requireThis(docId != null, "docId not null");
		DocTaskRecord taskRecord = this.getDSLContext().fetchOne(Tables.DOC_TASK, Tables.DOC_TASK.DOC_ID.eq(docId));
		if (taskRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + docId + "]");
		}
		return this.doLoad(docId, taskRecord);
	}

	@Override
	public List<DocTaskVRecord> doFind(QuerySpec querySpec) {
		return this.doFind(Tables.DOC_TASK_V, Tables.DOC_TASK_V.ID, querySpec);
	}

}
