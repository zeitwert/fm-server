
import { AggregateApiImpl, IGNORED_ATTRIBUTES } from "../../../../ddd/aggregate/service/impl/AggregateApiImpl";
import { PortfolioModel, PortfolioSnapshot } from "../../model/PortfolioModel";
import { PortfolioApi } from "../PortfolioApi";

const MODULE = "portfolio";
const PATH = "portfolios";
const TYPE = "portfolio";
const INCLUDES = "include[portfolio]=account";

export class PortfolioApiImpl extends AggregateApiImpl<PortfolioSnapshot> implements PortfolioApi {
	constructor() {
		const PROPS = Object.keys(PortfolioModel.properties);
		const IGNORED = IGNORED_ATTRIBUTES.concat(["documents"]);
		const ATTRIBUTES = PROPS.filter((el) => !IGNORED.includes(el));
		const RELATIONS = {
			//refObj: "obj",
			//documents: "document",
			account: "account",
			//holdings: "holding"
		};
		super(MODULE, PATH, TYPE, INCLUDES, ATTRIBUTES, RELATIONS);
	}
}
