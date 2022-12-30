import { API, Config, EntityRepository } from "../../../../app/common";
import { AggregateApiImpl, IGNORED_ATTRIBUTES } from "../../../../ddd/aggregate/service/impl/AggregateApiImpl";
import { DocumentModel, DocumentSnapshot } from "../../model/DocumentModel";
import { DocumentApi } from "../DocumentApi";

const MODULE = "dms";
const PATH = "documents";
const TYPE = "document";
const INCLUDES = "include[document]=templateDocument";

export class DocumentApiImpl extends AggregateApiImpl<DocumentSnapshot> implements DocumentApi {
	constructor() {
		const PROPS = Object.keys(DocumentModel.properties);
		const IGNORED = IGNORED_ATTRIBUTES.concat([
			"documents",
			"templateDocument",
			"questionnaire",
			"questionnaireResult"
		]);
		const ATTRIBUTES = PROPS.filter((el) => !IGNORED.includes(el));
		const RELATIONS = { templateDocument: "templateDocument" };
		super(MODULE, PATH, TYPE, INCLUDES, ATTRIBUTES, RELATIONS);
	}

	async getAvailableDocuments(): Promise<EntityRepository> {
		const response = await API.get(Config.getApiUrl(MODULE, PATH + "?filter[documentTypeId][NEQ]=instance"));
		return this.deserializeData(response.data);
	}
}
