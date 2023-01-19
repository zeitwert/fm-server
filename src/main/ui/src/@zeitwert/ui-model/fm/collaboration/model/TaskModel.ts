
import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { faTypes } from "../../../app/common";
import { Enumerated } from "../../../ddd/aggregate/model/EnumeratedModel";
import { DocModel } from "../../../ddd/doc/model/DocModel";

const MstTaskModel = DocModel.named("Task")
	.props({
		relatedTo: types.maybe(types.frozen<Enumerated>()),
		//account: types.maybe(types.reference(AccountModel)),
		subject: types.maybe(types.string),
		content: types.maybe(types.string),
		isPrivate: types.maybe(types.boolean),
		priority: types.maybe(types.frozen<Enumerated>()),
		dueAt: types.maybe(faTypes.dateWithOffset),
		remindAt: types.maybe(faTypes.dateWithOffset)
	});

type MstTaskType = typeof MstTaskModel;
interface MstTask extends MstTaskType { }

export const TaskModel: MstTask = MstTaskModel;
export type TaskModelType = typeof TaskModel;
export interface Task extends Instance<TaskModelType> { }
export type TaskSnapshot = SnapshotIn<TaskModelType>;
export type TaskPayload = Omit<TaskSnapshot, "id">;
