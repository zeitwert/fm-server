
import { EnumeratedField, IdField, NumberField, TextField } from "@zeitwert/ui-forms";
import { AccountModel } from "@zeitwert/ui-model";
import { Form } from "mstform";

const AccountFormModel = new Form(
	AccountModel,
	{
		id: new IdField(),
		tenant: new EnumeratedField({ source: "oe/objTenant", required: true }),
		owner: new EnumeratedField({ required: true, source: "oe/objUser" }),
		name: new TextField({ required: true }),
		key: new TextField(),
		description: new TextField(),
		//
		accountType: new EnumeratedField({ source: "account/codeAccountType", required: true }),
		clientSegment: new EnumeratedField({ source: "account/codeClientSegment" }),
		//
		inflationRate: new NumberField(),
	}
);

export default AccountFormModel;
