
import { EnumeratedField, IdField, NumberField, TextField } from "@zeitwert/ui-forms";
import { TenantModelType } from "@zeitwert/ui-model";
import { FormDefinition } from "mstform";

const TenantFormDef: FormDefinition<TenantModelType> = {
	id: new IdField(),
	key: new TextField(),
	name: new TextField({ required: true }),
	description: new TextField(),
	tenantType: new EnumeratedField({ source: "oe/codeTenantType", required: true }),
	//
	inflationRate: new NumberField(),
};

export default TenantFormDef;
