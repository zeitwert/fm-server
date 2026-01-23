import type { Enumerated } from "../../common/types";
import type { EntityMeta } from "../../common/api/jsonapi";

export interface PortfolioObject {
	id: string;
	name: string;
	itemType?: Enumerated;
}

export interface Portfolio {
	id: string;
	meta?: EntityMeta;
	name: string;
	portfolioNr?: string;
	description?: string;
	account?: Enumerated;
	tenant?: Enumerated;
	owner?: Enumerated;
	includes: PortfolioObject[];
	excludes: PortfolioObject[];
	buildings: PortfolioObject[];
}

export interface PortfolioListItem {
	id: string;
	name: string;
	account?: Enumerated;
	owner: Enumerated;
}
