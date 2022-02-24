import { EntityTypeRepository } from "../../../app/common";
import { ItemPartApi } from "../../item/service/ItemPartApi";
import { Doc } from "../model/DocModel";
import { DocPartTransitionSnapshot } from "../model/DocPartTransitionModel";
import { DocPartTransitionApiImpl } from "./impl/DocPartTransitionApiImpl";

export interface DocPartTransitionApi extends ItemPartApi<DocPartTransitionSnapshot> {
	find(doc: Doc): Promise<EntityTypeRepository>;
}

export const DOC_TRANSITION_API: DocPartTransitionApi = new DocPartTransitionApiImpl();
