
package io.zeitwert.fm.task.model.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.ddd.doc.model.base.DocRepositoryBase;
import io.zeitwert.ddd.obj.model.ObjRepository;
import io.zeitwert.ddd.session.model.RequestContext;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.DocTaskRepository;
import io.zeitwert.fm.task.model.base.DocTaskBase;
import io.zeitwert.fm.task.model.db.Tables;
import io.zeitwert.fm.task.model.db.tables.records.DocTaskVRecord;

@Component("docTaskRepository")
public class DocTaskRepositoryImpl extends DocRepositoryBase<DocTask, DocTaskVRecord>
		implements DocTaskRepository {

	private static final String AGGREGATE_TYPE = "doc_task";

	private final RequestContext requestCtx;

	protected DocTaskRepositoryImpl(AppContext appContext, RequestContext requestCtx) {
		super(DocTaskRepository.class, DocTask.class, DocTaskBase.class, AGGREGATE_TYPE, appContext);
		this.requestCtx = requestCtx;
	}

	@Override
	@PostConstruct
	public void registerPartRepositories() {
		super.registerPartRepositories();
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
