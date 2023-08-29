
import { EnumeratedField, IdField, NumberField, TextField } from "@zeitwert/ui-forms";
import { AccountModel, AccountModelType } from "@zeitwert/ui-model";
import { Form, FormDefinition } from "mstform";

export const AccountFormDef: FormDefinition<AccountModelType> = {
	id: new IdField(),
	tenant: new EnumeratedField({ source: "oe/objTenant", required: true }),
	owner: new EnumeratedField({ required: true, source: "oe/objUser" }),
	name: new TextField({ required: true }),
	description: new TextField(),
	//
	accountType: new EnumeratedField({ source: "account/codeAccountType", required: true }),
	clientSegment: new EnumeratedField({ source: "account/codeClientSegment" }),
	//
	inflationRate: new NumberField(),
	discountRate: new NumberField(),
};

const AccountForm = new Form(AccountModel, AccountFormDef);

export default AccountForm;
