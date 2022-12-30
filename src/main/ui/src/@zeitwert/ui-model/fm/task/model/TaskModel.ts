import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { faTypes } from "../../../app/common";
import { Enumerated } from "../../../ddd/aggregate/model/EnumeratedModel";
import { DocModel } from "../../../ddd/doc/model/DocModel";

const MstTaskModel = DocModel.named("Task").props({
	areas: types.optional(types.array(types.frozen<Enumerated>()), []),
	//
	taskPriority: types.maybe(types.frozen<Enumerated>()),
	dueDate: faTypes.date,
	reminderDate: types.maybe(faTypes.date)
});

type MstTaskType = typeof MstTaskModel;
export interface MstTask extends MstTaskType { }

export const TaskModel: MstTask = MstTaskModel;
export type TaskModelType = typeof TaskModel;
export interface Task extends Instance<TaskModelType> { }
export type TaskSnapshot = SnapshotIn<TaskModelType>;
export type TaskPayload = Omit<TaskSnapshot, "id">;
