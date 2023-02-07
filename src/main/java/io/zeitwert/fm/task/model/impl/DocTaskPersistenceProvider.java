package io.zeitwert.fm.task.model.impl;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.time.OffsetDateTime;

import org.jooq.DSLContext;
import org.jooq.exception.NoDataFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import io.zeitwert.ddd.persistence.jooq.AggregateState;
import io.zeitwert.ddd.persistence.jooq.base.DocExtnPersistenceProviderBase;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.DocTaskRepository;
import io.zeitwert.fm.task.model.base.DocTaskBase;
import io.zeitwert.fm.task.model.db.Tables;
import io.zeitwert.fm.task.model.db.tables.records.DocTaskRecord;

@Configuration("taskPersistenceProvider")
@DependsOn("codePartListTypeEnum")
public class DocTaskPersistenceProvider extends DocExtnPersistenceProviderBase<DocTask> {

	public DocTaskPersistenceProvider(DSLContext dslContext) {
		super(DocTaskRepository.class, DocTaskBase.class, dslContext);
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
	public Class<?> getEntityClass() {
		return DocTask.class;
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

}
