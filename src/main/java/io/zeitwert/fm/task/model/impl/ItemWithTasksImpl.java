package io.zeitwert.fm.task.model.impl;

import java.util.List;

import io.zeitwert.ddd.aggregate.model.Aggregate;
import io.zeitwert.ddd.app.service.api.AppContext;
import io.zeitwert.fm.task.model.DocTask;
import io.zeitwert.fm.task.model.DocTaskRepository;
import io.zeitwert.fm.task.model.ItemWithTasks;
import io.zeitwert.fm.task.model.db.tables.records.DocTaskVRecord;

public class ItemWithTasksImpl implements ItemWithTasks {

	private final Aggregate aggregate;
	private final DocTaskRepository taskRepository;

	public ItemWithTasksImpl(Aggregate aggregate) {
		this.aggregate = aggregate;
		this.taskRepository = AppContext.getInstance().getBean(DocTaskRepository.class);
	}

	@Override
	public List<DocTaskVRecord> getTasks() {
		return this.taskRepository.getByForeignKey("related_to_obj_id", this.aggregate.getId());
	}

	@Override
	public DocTask addTask() {
		DocTask task = this.taskRepository.create(this.aggregate.getTenantId());
		task.setRelatedToId(this.aggregate.getId());
		return task;
	}

}
