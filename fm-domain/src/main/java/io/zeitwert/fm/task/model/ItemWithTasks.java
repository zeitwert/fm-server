package io.zeitwert.fm.task.model;

import io.zeitwert.fm.task.model.db.tables.records.DocTaskVRecord;

import java.util.List;

public interface ItemWithTasks {

	List<DocTaskVRecord> getTasks();

	DocTask addTask();

}
