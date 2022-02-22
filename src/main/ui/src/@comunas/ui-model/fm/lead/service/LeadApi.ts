import { AggregateApi } from "../../../ddd/aggregate/service/AggregateApi";
import { ConversionInfo, LeadSnapshot } from "../model/LeadModel";
import { LeadApiImpl } from "./impl/LeadApiImpl";

export interface LeadApi extends AggregateApi<LeadSnapshot> {
	convertLead(conversionInfo: ConversionInfo): Promise<any>;
}

export const LEAD_API: LeadApi = new LeadApiImpl();
