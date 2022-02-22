
import { AggregateApi } from "../../../ddd/aggregate/service/AggregateApi";
import { PortfolioSnapshot } from "../model/PortfolioModel";
import { PortfolioApiImpl } from "./impl/PortfolioApiImpl";

export interface PortfolioApi extends AggregateApi<PortfolioSnapshot> { }

export const PORTFOLIO_API: PortfolioApi = new PortfolioApiImpl();
