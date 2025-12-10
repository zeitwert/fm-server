package io.zeitwert.fm.task.model.impl;

import java.util.List;

import io.dddrive.ddd.model.Aggregate;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.DocTaskRepository;
import io.zeitwert.fm.task.model.ItemWithTasks;
import io.zeitwert.fm.task.model.db.tables.records.DocTaskVRecord;

public interface AggregateWithTasksMixin extends ItemWithTasks {

	Aggregate aggregate();

	DocTaskRepository taskRepository();

	@Override
	default List<DocTaskVRecord> getTasks() {
		return this.taskRepository().getByForeignKey("related_to_obj_id", this.aggregate().getId());
	}

	@Override
	default DocTask addTask() {
		DocTask task = this.taskRepository().create(this.aggregate().getTenantId());
		task.setRelatedToId(this.aggregate().getId());
		return task;
	}

}
