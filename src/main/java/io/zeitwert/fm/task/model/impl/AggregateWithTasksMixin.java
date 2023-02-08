package io.zeitwert.fm.task.model.impl;

import java.util.List;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.DocTaskRepository;
import io.zeitwert.fm.task.model.ItemWithTasks;
import io.zeitwert.fm.task.model.db.tables.records.DocTaskVRecord;

public interface AggregateWithTasksMixin extends ItemWithTasks {

	static DocTaskRepository taskRepository() {
		return AppContext.getInstance().getBean(DocTaskRepository.class);
	}

	Aggregate aggregate();

	@Override
	default List<DocTaskVRecord> getTasks() {
		return taskRepository().getByForeignKey("related_to_obj_id", this.aggregate().getId());
	}

	@Override
	default DocTask addTask() {
		DocTask task = taskRepository().create(this.aggregate().getTenantId());
		task.setRelatedToId(this.aggregate().getId());
		return task;
	}

}
