
import { IdField, TextField } from "@zeitwert/ui-forms";
import { PortfolioModelType } from "@zeitwert/ui-model";
import { FormDefinition } from "mstform";

const PortfolioFormDef: FormDefinition<PortfolioModelType> = {
	id: new IdField(),
	name: new TextField({ required: true }),
	portfolioNr: new TextField(),
	description: new TextField(),
};

export default PortfolioFormDef;
