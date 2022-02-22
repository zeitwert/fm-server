import { ItemPartApi } from "../../item/service/ItemPartApi";
import { ObjPartTransitionSnapshot } from "../model/ObjPartTransitionModel";
import { ObjPartTransitionApiImpl } from "./impl/ObjPartTransitionApiImpl";

export interface ObjPartTransitionApi extends ItemPartApi<ObjPartTransitionSnapshot> { }

export const OBJ_TRANSITION_API: ObjPartTransitionApi = new ObjPartTransitionApiImpl();
