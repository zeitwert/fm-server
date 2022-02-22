import { KanbanApiImpl } from "./impl/KanbanApiImpl";

export interface KanbanApi {
	getHeaders(path: string): Promise<any>;

	updateItem(itemPath: string, itemType: string, itemId: string, field: string, id: string): Promise<any>;
}

export const KANBAN_API = new KanbanApiImpl();
