
import { EnumeratedField, IdField, TextField } from "@zeitwert/ui-forms";
import { UserModel, UserModelType } from "@zeitwert/ui-model";
import { Form, FormDefinition } from "mstform";

export const UserFormDef: FormDefinition<UserModelType> = {
	id: new IdField(),
	tenant: new EnumeratedField({ source: "oe/objTenant", required: true }),
	owner: new EnumeratedField({ required: true, source: "oe/objUser" }),
	email: new TextField({ required: true }),
	name: new TextField({ required: true }),
	password: new TextField({ required: true }),
	role: new EnumeratedField({ source: "oe/codeUserRole", required: true }),
	description: new TextField(),
};

const UserForm = new Form(UserModel, UserFormDef);

export default UserForm;
