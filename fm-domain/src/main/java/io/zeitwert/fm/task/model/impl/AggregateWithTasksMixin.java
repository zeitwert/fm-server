package io.zeitwert.fm.task.model.impl;

import java.time.OffsetDateTime;
import java.util.List;

import io.dddrive.core.ddd.model.Aggregate;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.DocTaskRepository;
import io.zeitwert.fm.task.model.ItemWithTasks;

public interface AggregateWithTasksMixin extends ItemWithTasks {

	Aggregate aggregate();

	DocTaskRepository taskRepository();

	@Override
	default List<DocTask> getTasks() {
		return this.taskRepository().getByForeignKey("related_obj_id", this.aggregate().getId());
	}

	@Override
	default DocTask addTask() {
		DocTask task = this.taskRepository().create(this.aggregate().getTenantId(), null, OffsetDateTime.now());
		task.setRelatedToId((Integer) this.aggregate().getId());
		return task;
	}

}
