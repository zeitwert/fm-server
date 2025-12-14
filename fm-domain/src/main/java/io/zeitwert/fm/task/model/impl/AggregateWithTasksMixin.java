package io.zeitwert.fm.task.model.impl;

import io.dddrive.core.ddd.model.Aggregate;
import io.dddrive.core.obj.model.Obj;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.DocTaskRepository;
import io.zeitwert.fm.task.model.ItemWithTasks;

import java.time.OffsetDateTime;
import java.util.List;

public interface AggregateWithTasksMixin extends ItemWithTasks {

	Aggregate aggregate();

	DocTaskRepository taskRepository();

	@Override
	default List<DocTask> getTasks() {
		Object id = this.aggregate().getId();
		String fkName = (this instanceof Obj) ? "relatedObjId" : "relatedDocId";
		return this.taskRepository().getByForeignKey(fkName, id).stream().map(it -> taskRepository().get(it)).toList();
	}

	@Override
	default DocTask addTask() {
		DocTask task = this.taskRepository().create(this.aggregate().getTenantId(), null, OffsetDateTime.now());
		task.setRelatedToId((Integer) this.aggregate().getId());
		return task;
	}

}
