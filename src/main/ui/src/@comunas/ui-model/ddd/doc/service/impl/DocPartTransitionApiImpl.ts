import { API, Config, EntityTypeRepository } from "../../../../app/common";
import { IGNORED_ATTRIBUTES } from "../../../aggregate/service/impl/AggregateApiImpl";
import { ItemPartApiImpl } from "../../../item/service/impl/ItemPartApiImpl";
import { Doc } from "../../model/DocModel";
import { DocPartTransitionModel, DocPartTransitionSnapshot } from "../../model/DocPartTransitionModel";
import { DocPartTransitionApi } from "../DocPartTransitionApi";

const MODULE = "doc";
const PATH = "docs";
const PART_PATH = "transitions";
const TYPE = "docTransition";
const INCLUDES = "include[docTransition]=doc";

export class DocPartTransitionApiImpl
	extends ItemPartApiImpl<DocPartTransitionSnapshot>
	implements DocPartTransitionApi {
	constructor() {
		const PROPS = Object.keys(DocPartTransitionModel.properties);
		const IGNORED = IGNORED_ATTRIBUTES.concat([]);
		const ATTRIBUTES = PROPS.filter((el) => !IGNORED.includes(el));
		const RELATIONS = { doc: "doc" };
		super(MODULE, PATH, PART_PATH, TYPE, INCLUDES, ATTRIBUTES, RELATIONS);
	}

	async find(doc: Doc): Promise<EntityTypeRepository> {
		const suffix = "?" + INCLUDES + "&filter[docId]=" + doc.id;
		const response = await API.get(Config.getApiUrl(MODULE, PART_PATH) + suffix);
		return this.deserializeData(response.data);
	}
}
