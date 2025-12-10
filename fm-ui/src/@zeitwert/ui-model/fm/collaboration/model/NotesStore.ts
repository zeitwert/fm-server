
import { transaction } from "mobx";
import { flow, Instance, SnapshotIn, types } from "mobx-state-tree";
import { EntityTypeRepository } from "../../../app";
import { StoreWithEntitiesModel } from "../../../ddd/aggregate/model/StoreWithEntities";
import { NOTE_API } from "../service/NoteApi";
import { Note, NoteModel } from "./NoteModel";

const MstNotesStoreModel = StoreWithEntitiesModel
	.named("NotesStore")
	.props({
		notes: types.optional(types.array(NoteModel), []),
	})
	.views((self) => ({
		getNote(id: string): Note | undefined {
			return self.notes.find(n => n.id === id);
		},
	}))
	.actions((self) => ({
		async load(relatedToId: string): Promise<void> {
			return flow<void, any[]>(function* (): any {
				try {
					const repository: EntityTypeRepository = yield NOTE_API.getAggregates("filter[relatedToId]=" + relatedToId);
					self.notes.clear();
					const notesRepo = repository["note"];
					if (notesRepo) {
						transaction(() => {
							Object.keys(notesRepo)
								.map((id) => notesRepo[id])
								.sort((a, b) => (a.meta.createdAt > b.meta.createdAt ? -1 : 1))
								.forEach((snapshot) => self.notes.push(snapshot));
						});
					}
				} catch (error: any) {
					console.error("Failed to load notes", error);
					return Promise.reject(error);
				}
			})();
		},
	}));

type MstNotesStoreType = typeof MstNotesStoreModel;
interface MstNotesStore extends MstNotesStoreType { }

export const NotesStoreModel: MstNotesStore = MstNotesStoreModel;
export type NotesStoreModelType = typeof NotesStoreModel;
export interface NotesStore extends Instance<NotesStoreModelType> { }
export type NotesStoreSnapshot = SnapshotIn<NotesStoreModelType>;
export type NotesStorePayload = Omit<NotesStoreSnapshot, "id">;
