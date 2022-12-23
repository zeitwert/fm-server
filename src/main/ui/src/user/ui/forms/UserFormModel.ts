
import { EnumeratedField, IdField, TextField } from "@zeitwert/ui-forms";
import { UserModel } from "@zeitwert/ui-model";
import { Form } from "mstform";

const UserFormModel = new Form(
	UserModel,
	{
		id: new IdField(),
		tenant: new EnumeratedField({ source: "oe/objTenant", required: true }),
		owner: new EnumeratedField({ required: true, source: "oe/objUser" }),
		email: new TextField({ required: true }),
		name: new TextField({ required: true }),
		password: new TextField({ required: true }),
		role: new EnumeratedField({ source: "oe/codeUserRole", required: true }),
		description: new TextField(),
	}
);

export default UserFormModel;
