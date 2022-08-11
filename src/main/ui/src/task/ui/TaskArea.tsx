import { EntityType, session, TaskStoreModel } from "@zeitwert/ui-model";
import ItemsPage from "item/ui/ItemsPage";
import React from "react";
import { Route, Routes } from "react-router-dom";
import TaskPage from "./TaskPage";

export default class TaskArea extends React.Component {
	render() {
		session.setHelpContext(EntityType.TASK);
		return (
			<Routes>
				<Route
					path=""
					element={
						<ItemsPage
							entityType={EntityType.TASK}
							store={TaskStoreModel.create({})}
							listDatamart="task.tasks"
							listTemplate="task.tasks.my-open"
						/>
					}
				/>
				<Route path=":taskId" element={<TaskPage />} />
			</Routes>
		);
	}
}
