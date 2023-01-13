
import { DateField, EnumeratedField, IdField, TextField } from "@zeitwert/ui-forms";
import { AggregateField } from "@zeitwert/ui-forms/model/AggregateField";
import { Account, ACCOUNT_API, ContactModelType } from "@zeitwert/ui-model";
import { FormDefinition } from "mstform";

const ContactFormDef: FormDefinition<ContactModelType> = {
	id: new IdField(),
	tenant: new EnumeratedField({ source: "oe/objTenant", required: true }),
	owner: new EnumeratedField({ required: true, source: "oe/objUser" }),
	description: new TextField(),
	//
	account: new AggregateField<Account>({ source: ACCOUNT_API }),
	//
	contactRole: new EnumeratedField({ source: "contact/codeContactRole" }),
	salutation: new EnumeratedField({ required: true, source: "contact/codeSalutation" }),
	title: new EnumeratedField({ source: "contact/codeTitle" }),
	firstName: new TextField(),
	lastName: new TextField({ required: true }),
	birthDate: new DateField(),
	//
	mobile: new TextField(),
	email: new TextField(),
	phone: new TextField(),
};

export default ContactFormDef;
