import { TaskPayload, UserInfo } from "@zeitwert/ui-model";
import { TaskFormState } from "activity/forms/TaskForm";
import { toJS } from "mobx";
import moment from "moment";

export class FormParser {

	static parseTask(data: TaskFormState, owner: UserInfo): TaskPayload {
		const dueDate = moment.utc(data.dueDate);
		const reminderDate = dueDate.clone();
		if (data.reminderOption) {
			reminderDate.subtract(data.reminderOption.id, "minutes");
		}
		return Object.assign({}, data, {
			taskPriority: toJS(data.priority),
			dueDate: data.dueDate ? dueDate.toDate() : undefined,
			reminderDate: data.reminderSet ? reminderDate.toDate() : undefined,
			account: data.account?.id,
			owner: owner,
			assignee: data.assignee?.id
		});
	}

}
