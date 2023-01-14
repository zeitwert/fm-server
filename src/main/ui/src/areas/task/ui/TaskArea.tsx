import { EntityType, session, Task, TaskStore, TaskStoreModel } from "@zeitwert/ui-model";
import ItemsPage from "lib/item/ui/ItemsPage";
import React from "react";
import { Route, Routes } from "react-router-dom";
import TaskCreationForm from "./TaskCreationForm";
import TaskPage from "./TaskPage";

const taskStore = TaskStoreModel.create({});

export default class TaskArea extends React.Component {

	componentDidMount(): void {
		session.setHelpContext(EntityType.TASK);
	}

	render() {
		return (
			<Routes>
				<Route
					path=""
					element={
						<ItemsPage
							entityType={EntityType.TASK}
							store={taskStore}
							listDatamart="collaboration.tasks"
							listTemplate="collaboration.tasks.my-open"
							canCreate={true}
							createEditor={() => <TaskCreationForm store={taskStore} />}
							onAfterCreate={(store: TaskStore) => { initTask(store.task!) }}
						/>
					}
				/>
				<Route path=":taskId" element={<TaskPage />} />
			</Routes>
		);
	}

}

const initTask = (task: Task) => {
	task.setField("tenant", session.sessionInfo?.tenant);
	if (!session.isKernelTenant) {
		task.setAccount(session.sessionInfo?.account?.id);
	}
}
