
import { DateField, EnumeratedField, IdField, TextField } from "@zeitwert/ui-forms";
import { AggregateField } from "@zeitwert/ui-forms/model/AggregateField";
import { BooleanField } from "@zeitwert/ui-forms/model/BooleanField";
import { ACCOUNT_API, TaskModel, TaskModelType } from "@zeitwert/ui-model";
import { Form, FormDefinition } from "mstform";

export const TaskFormDef: FormDefinition<TaskModelType> = {
	id: new IdField(),
	tenant: new EnumeratedField({ required: true, source: "oe/objTenant" }),
	owner: new EnumeratedField({ required: true, source: "oe/objUser" }),
	//
	relatedTo: new EnumeratedField({ required: true, source: "oe/objUser" }),
	account: new AggregateField({ required: true, source: ACCOUNT_API }),
	//
	subject: new TextField({ required: true }),
	content: new TextField(),
	isPrivate: new BooleanField(),
	priority: new EnumeratedField({ required: true, source: "task/codeTaskPriority" }),
	dueAt: new DateField({ required: true }),
	remindAt: new DateField(),
};


const TaskForm = new Form(TaskModel, TaskFormDef);

export default TaskForm;
