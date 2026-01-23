import { createEntityApi } from "../../common/api/entityApi";
import type { Tenant, TenantListItem } from "./types";

export const tenantApi = createEntityApi<Tenant>({
	module: "oe",
	path: "tenants",
	type: "tenant",
	includes: "include[tenant]=logo",
	relations: {
		logo: "document",
	},
});

export const tenantListApi = createEntityApi<TenantListItem>({
	module: "oe",
	path: "tenants",
	type: "tenant",
	includes: "",
	relations: {},
});
