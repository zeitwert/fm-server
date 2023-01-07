import { EntityType, session, TaskStoreModel } from "@zeitwert/ui-model";
import ItemsPage from "lib/item/ui/ItemsPage";
import React from "react";
import { Route, Routes } from "react-router-dom";
import TaskPage from "./TaskPage";

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
