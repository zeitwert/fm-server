
import { EntityTypeRepository } from "@zeitwert/ui-model/app";
import { transaction } from "mobx";
import { applySnapshot, flow, Instance, SnapshotIn, types } from "mobx-state-tree";
import { StoreWithEntitiesModel } from "../../aggregate/model/StoreWithEntities";
import { NOTE_API } from "../service/NoteApi";
import { Note, NoteModel, NotePayload, NoteSnapshot } from "./NoteModel";

const MstStoreWithNotesModel = StoreWithEntitiesModel
	.named("StoreWithNotes")
	.props({
		notes: types.optional(types.array(NoteModel), []),
	})
	.views((self) => ({
		getNote(id: string): Note | undefined {
			return self.notes.find(n => n.id === id);
		},
	}))
	.actions((self) => ({
		async loadNotes(relatedToId: string): Promise<void> {
			return flow<void, any[]>(function* (): any {
				try {
					const repository: EntityTypeRepository = yield NOTE_API.getAggregates("filter[relatedToId]=" + relatedToId);
					const notesRepo = repository["note"];
					if (notesRepo) {
						transaction(() => {
							self.notes.clear();
							Object.keys(notesRepo)
								.map((id) => notesRepo[id])
								.forEach((snapshot) => self.notes.push(snapshot));
						});
					}
				} catch (error: any) {
					console.error("Failed to load notes", error);
					return Promise.reject(error);
				}
			})();
		},
		async addNote(relatedToId: string, notePayload: NotePayload): Promise<Note> {
			const note: NoteSnapshot = Object.assign({}, notePayload, { id: "New:" + new Date().getTime(), relatedToId: relatedToId });
			return flow<Note, any[]>(function* (): any {
				try {
					const repository: EntityTypeRepository = yield NOTE_API.createAggregate(note);
					const notesRepo = repository["note"];
					if (notesRepo) {
						transaction(() => {
							Object.keys(notesRepo)
								.map((id) => notesRepo[id])
								.forEach((snapshot) => self.notes.push(snapshot));
						});
					}
				} catch (error: any) {
					console.error("Failed to add note", note, error);
					return Promise.reject(error);
				}
			})();
		},
		async storeNote(id: string, notePayload: NotePayload): Promise<Note> {
			const note = Object.assign({}, notePayload, { id: id });
			return flow<Note, any[]>(function* (): any {
				try {
					const repository: EntityTypeRepository = yield NOTE_API.storeAggregate(note);
					const notesRepo = repository["note"];
					if (notesRepo) {
						let note: Note = self.getNote(id)!;
						applySnapshot(note, notesRepo[id]);
					}
				} catch (error: any) {
					console.error("Failed to store note", note, error);
					return Promise.reject(error);
				}
			})();
		},
		async removeNote(id: string): Promise<Note> {
			return flow<Note, any[]>(function* (): any {
				try {
					const note = self.getNote(id);
					if (!!note) {
						const index = self.notes.indexOf(note);
						yield NOTE_API.deleteAggregate(id);
						self.notes.splice(index, 1);
					}
				} catch (error: any) {
					console.error("Failed to remove note", id, error);
					return Promise.reject(error);
				}
			})();
		},
	}));

type MstStoreWithNotesType = typeof MstStoreWithNotesModel;
interface MstStoreWithNotes extends MstStoreWithNotesType { }

export const StoreWithNotesModel: MstStoreWithNotes = MstStoreWithNotesModel;
export type StoreWithNotesModelType = typeof StoreWithNotesModel;
export interface StoreWithNotes extends Instance<StoreWithNotesModelType> { }
export type StoreWithNotesSnapshot = SnapshotIn<StoreWithNotesModelType>;
export type StoreWithNotesPayload = Omit<StoreWithNotesSnapshot, "id">;
