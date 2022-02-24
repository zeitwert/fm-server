import { FormApiImpl } from "./impl/FormApiImpl";

export interface FormApi {
	getDefinition(id: string): Promise<any>;

	getMetadataDefinition(id: string): Promise<any>;
}

export const FORM_API: FormApi = new FormApiImpl();
