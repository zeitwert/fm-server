import { createEntityApi } from "@/common/api/entityApi";
import type { Portfolio, PortfolioListItem } from "./types";

export const portfolioApi = createEntityApi<Portfolio>({
	module: "portfolio",
	path: "portfolios",
	type: "portfolio",
	includes: "include[portfolio]=account",
	relations: {
		account: "account",
	},
});

export const portfolioListApi = createEntityApi<PortfolioListItem>({
	module: "portfolio",
	path: "portfolios",
	type: "portfolio",
	includes: "",
	relations: {},
});

// Building list API for portfolio includes/excludes dropdown
export interface BuildingListItem {
	id: string;
	name: string;
	caption?: string;
}

export const buildingListApi = createEntityApi<BuildingListItem>({
	module: "building",
	path: "buildings",
	type: "building",
	includes: "",
	relations: {},
});
