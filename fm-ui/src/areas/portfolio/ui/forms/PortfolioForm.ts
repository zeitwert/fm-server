
import { IdField, TextField } from "@zeitwert/ui-forms";
import { PortfolioModel, PortfolioModelType } from "@zeitwert/ui-model";
import { Form, FormDefinition } from "mstform";

export const PortfolioFormDef: FormDefinition<PortfolioModelType> = {
	id: new IdField(),
	name: new TextField({ required: true }),
	portfolioNr: new TextField(),
	description: new TextField(),
};

const PortfolioForm = new Form(PortfolioModel, PortfolioFormDef);

export default PortfolioForm;
