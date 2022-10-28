
import { AggregateApi } from "../../../ddd/aggregate/service/AggregateApi";
import { TenantSnapshot } from "../model/TenantModel";
import { TenantApiImpl } from "./impl/TenantApiImpl";

export interface TenantApi extends AggregateApi<TenantSnapshot> { }

export const TENANT_API: TenantApi = new TenantApiImpl();
