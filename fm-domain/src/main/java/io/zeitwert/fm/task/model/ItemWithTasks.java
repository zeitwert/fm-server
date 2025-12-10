package io.zeitwert.fm.task.model;

import java.util.List;

public interface ItemWithTasks {

	List<DocTask> getTasks();

	DocTask addTask();

}
