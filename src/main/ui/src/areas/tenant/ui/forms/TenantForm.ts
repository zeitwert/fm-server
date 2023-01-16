
import { EnumeratedField, IdField, NumberField, TextField } from "@zeitwert/ui-forms";
import { TenantModel, TenantModelType } from "@zeitwert/ui-model";
import { Form, FormDefinition } from "mstform";

export const TenantFormDef: FormDefinition<TenantModelType> = {
	id: new IdField(),
	key: new TextField(),
	name: new TextField({ required: true }),
	description: new TextField(),
	tenantType: new EnumeratedField({ source: "oe/codeTenantType", required: true }),
	//
	inflationRate: new NumberField(),
};

const TenantForm = new Form(TenantModel, TenantFormDef);

export default TenantForm;
