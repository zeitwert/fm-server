
import { EnumeratedField, IdField, NumberField, TextField } from "@zeitwert/ui-forms";
import { TenantModel } from "@zeitwert/ui-model";
import { Form } from "mstform";

const TenantFormModel = new Form(
	TenantModel,
	{
		id: new IdField(),
		key: new TextField(),
		name: new TextField({ required: true }),
		description: new TextField(),
		tenantType: new EnumeratedField({ source: "oe/codeTenantType", required: true }),
		//
		inflationRate: new NumberField(),
	}
);

export default TenantFormModel;
