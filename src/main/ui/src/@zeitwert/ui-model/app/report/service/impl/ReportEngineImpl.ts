import { requireThis } from "../../../common";
import { Datamart } from "../../model/Datamart";
import { DatamartImpl } from "../../model/impl/DatamartImpl";
import { LayoutImpl } from "../../model/impl/LayoutImpl";
import { Layout } from "../../model/Layout";
import { Template } from "../../model/Template";
import { ReportEngine } from "../ReportEngine";

export class ReportEngineImpl implements ReportEngine {
	executeTemplate(template: Template) {
		return this.execute(template.datamart, template.layout, template.params);
	}

	execute(datamart: Datamart, layout: Layout, params?: { [key: string]: any }, sort?: string, limit?: number) {
		requireThis(!!datamart && datamart instanceof DatamartImpl, "valid datamart");
		requireThis(!!layout && layout instanceof LayoutImpl, "valid layout");
		return datamart.provider.execute(datamart, layout, params, sort, limit);
	}
}
