import { EntityRepository } from "../../../app/common";
import { AggregateApi } from "../../../ddd/aggregate/service/AggregateApi";
import { DocumentSnapshot } from "../model/DocumentModel";
import { DocumentApiImpl } from "./impl/DocumentApiImpl";

export interface DocumentApi extends AggregateApi<DocumentSnapshot> {
	getAvailableDocuments(): Promise<EntityRepository>;
}

export const DOCUMENT_API: DocumentApi = new DocumentApiImpl();
