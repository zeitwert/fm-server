
import { EntityTypeRepository } from "../../../../ui-model/app/common/service/JsonApi";
import { AggregateApi } from "../../../ddd/aggregate/service/AggregateApi";
import { ContactSnapshot } from "../model/ContactModel";
import { ContactApiImpl } from "./impl/ContactApiImpl";

export interface ContactApi extends AggregateApi<ContactSnapshot> {
	getByEmail(email: string): Promise<EntityTypeRepository>;
	getAllLifeEvents(): Promise<EntityTypeRepository>;
}

export const CONTACT_API: ContactApi = new ContactApiImpl();
