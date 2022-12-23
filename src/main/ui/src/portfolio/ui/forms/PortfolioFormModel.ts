
import { IdField, TextField } from "@zeitwert/ui-forms";
import { PortfolioModel } from "@zeitwert/ui-model";
import { Form } from "mstform";

const PortfolioFormModel = new Form(
	PortfolioModel,
	{
		id: new IdField(),
		name: new TextField({ required: true }),
		portfolioNr: new TextField(),
		description: new TextField(),
	}
);

export default PortfolioFormModel;
