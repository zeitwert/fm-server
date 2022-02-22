import { API, Config } from "../../../common";
import { FormApi } from "../FormApi";

export class FormApiImpl implements FormApi {
	async getDefinition(id: string): Promise<any> {
		const [moduleId, formId] = id.split("/");
		const response = await API.get(Config.getModuleConfigUrl("t1", moduleId, "forms/" + formId));
		return response.data.formDef;
	}

	async getMetadataDefinition(id: string): Promise<any> {
		const response = await API.get(Config.getModuleConfigUrl("t1", "metadata", "forms/" + id));
		return response.data;
	}

}
