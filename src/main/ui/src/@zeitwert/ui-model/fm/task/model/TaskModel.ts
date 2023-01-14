import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { faTypes } from "../../../app/common";
import { Enumerated } from "../../../ddd/aggregate/model/EnumeratedModel";
import { DocModel } from "../../../ddd/doc/model/DocModel";
import { AccountModel } from "../../account/model/AccountModel";
import { TaskStore } from "./TaskStore";

const MstTaskModel = DocModel.named("Task")
	.props({
		relatedTo: types.maybe(types.frozen<Enumerated>()),
		account: types.maybe(types.reference(AccountModel)),
		subject: types.maybe(types.string),
		content: types.maybe(types.string),
		isPrivate: types.maybe(types.boolean),
		priority: types.maybe(types.frozen<Enumerated>()),
		dueAt: types.maybe(faTypes.dateWithOffset),
		remindAt: types.maybe(faTypes.dateWithOffset)
	})
	.actions((self) => {
		const superSetField = self.setField;
		async function setAccount(id: string | undefined) {
			id && (await (self.rootStore as TaskStore).accountsStore.loadAccount(id));
			return superSetField("account", id);
		}
		async function setField(field: string, value: any) {
			switch (field) {
				case "account": {
					return setAccount(value);
				}
				default: {
					return superSetField(field, value);
				}
			}
		}
		return {
			setAccount,
			setField
		};
	});

type MstTaskType = typeof MstTaskModel;
interface MstTask extends MstTaskType { }

export const TaskModel: MstTask = MstTaskModel;
export type TaskModelType = typeof TaskModel;
export interface Task extends Instance<TaskModelType> { }
export type TaskSnapshot = SnapshotIn<TaskModelType>;
export type TaskPayload = Omit<TaskSnapshot, "id">;
