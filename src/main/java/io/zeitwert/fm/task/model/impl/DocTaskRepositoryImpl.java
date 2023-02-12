
package io.zeitwert.fm.task.model.impl;

import static io.dddrive.util.Invariant.requireThis;

import java.time.OffsetDateTime;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import io.dddrive.app.model.RequestContext;
import io.dddrive.app.service.api.AppContext;
import io.dddrive.jooq.ddd.AggregateState;
import io.dddrive.jooq.doc.JooqDocExtnRepositoryBase;
import io.dddrive.obj.model.ObjRepository;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.DocTaskRepository;
import io.zeitwert.fm.task.model.base.DocTaskBase;
import io.zeitwert.fm.task.model.db.Tables;
import io.zeitwert.fm.task.model.db.tables.records.DocTaskRecord;
import io.zeitwert.fm.task.model.db.tables.records.DocTaskVRecord;

@Component("docTaskRepository")
public class DocTaskRepositoryImpl extends JooqDocExtnRepositoryBase<DocTask, DocTaskVRecord>
		implements DocTaskRepository {

	private static final String AGGREGATE_TYPE = "doc_task";

	private final RequestContext requestCtx;

	protected DocTaskRepositoryImpl(AppContext appContext, DSLContext dslContext, RequestContext requestCtx) {
		super(DocTaskRepository.class, DocTask.class, DocTaskBase.class, AGGREGATE_TYPE, appContext, dslContext);
		this.requestCtx = requestCtx;
	}

	@Override
	public void mapProperties() {
		super.mapProperties();
		this.mapField("relatedObjId", AggregateState.EXTN, "related_obj_id", Integer.class);
		this.mapField("relatedDocId", AggregateState.EXTN, "related_doc_id", Integer.class);
		this.mapField("subject", AggregateState.EXTN, "subject", String.class);
		this.mapField("content", AggregateState.EXTN, "content", String.class);
		this.mapField("isPrivate", AggregateState.EXTN, "is_private", Boolean.class);
		this.mapField("priority", AggregateState.EXTN, "priority_id", String.class);
		this.mapField("dueAt", AggregateState.EXTN, "due_at", OffsetDateTime.class);
		this.mapField("remindAt", AggregateState.EXTN, "remind_at", OffsetDateTime.class);
	}

	@Override
	public DocTask doCreate() {
		return this.doCreate(this.dslContext().newRecord(Tables.DOC_TASK));
	}

	@Override
	public DocTask doLoad(Integer objId) {
		requireThis(objId != null, "objId not null");
		DocTaskRecord taskRecord = this.dslContext().fetchOne(Tables.DOC_TASK, Tables.DOC_TASK.DOC_ID.eq(objId));
		if (taskRecord == null) {
			throw new NoDataFoundException(this.getClass().getSimpleName() + "[" + objId + "]");
		}
		return this.doLoad(objId, taskRecord);
	}

	@Override
	public List<DocTaskVRecord> doFind(QuerySpec uiQuerySpec) {
		PathSpec relatedToIdField = PathSpec.of("relatedToId");
		QuerySpec dbQuerySpec = null;
		if (uiQuerySpec.findFilter(relatedToIdField).isPresent()) {
			dbQuerySpec = new QuerySpec(DocTaskVRecord.class);
			Integer relatedToId = uiQuerySpec.findFilter(relatedToIdField).get().getValue();
			if (ObjRepository.isObjId(relatedToId)) {
				PathSpec relatedToObjIdField = PathSpec.of("related_obj_id");
				dbQuerySpec.addFilter(relatedToObjIdField.filter(FilterOperator.EQ, relatedToId));
			} else {
				PathSpec relatedToDocIdField = PathSpec.of("related_doc_id");
				dbQuerySpec.addFilter(relatedToDocIdField.filter(FilterOperator.EQ, relatedToId));
			}
			uiQuerySpec.getFilters()
					.stream()
					.filter(f -> !f.getPath().equals(relatedToIdField))
					.forEach(dbQuerySpec::addFilter);
		} else {
			dbQuerySpec = uiQuerySpec;
		}
		List<DocTaskVRecord> tasks = this.doFind(Tables.DOC_TASK_V, Tables.DOC_TASK_V.ID, dbQuerySpec);
		Integer userId = this.requestCtx.getUser().getId();
		tasks.removeIf(t -> !this.isVisible(t, userId));
		tasks.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
		return tasks;
	}

	private boolean isVisible(DocTaskVRecord task, Integer userId) {
		return !task.getIsPrivate() || userId.equals(task.getCreatedByUserId()) || userId.equals(task.getOwnerId());
	}

}
