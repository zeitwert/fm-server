import { AggregateApi } from "../../../ddd/aggregate/service/AggregateApi";
import { BuildingSnapshot } from "../model/BuildingModel";
import { BuildingApiImpl } from "./impl/BuildingApiImpl";

export interface BuildingApi extends AggregateApi<BuildingSnapshot> { }

export const BUILDING_API: BuildingApi = new BuildingApiImpl();
