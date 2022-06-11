
import { session, UUID } from "@zeitwert/ui-model";
import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { string } from "mobx-state-tree/dist/internal";
import { ItemPartNote, ItemPartNoteModel, ItemPartNotePayload } from "./ItemPartNoteModel";

const MstItemPartNoteStoreModel = types.model("ItemPartNoteStore")
	.props({
		aggregateId: string,
		parentId: types.maybe(string),
		notes: types.optional(types.array(ItemPartNoteModel), [])
	})
	.actions((self) => ({
		addNote(note?: ItemPartNotePayload): ItemPartNote {
			const newNote = Object.assign(
				{},
				note,
				{
					id: "New:" + UUID(),
					createdAt: new Date(),
					createdByUser: session.sessionInfo?.user
				}
			);
			self.notes.push(newNote);
			return self.notes.at(self.notes.length - 1)!;
		},
		removeNote(id: string): void {
			self.notes.remove(self.notes.find((n) => n.id === id)!);
		}
	}))
	.views((self) => ({
		getNote(id: string): ItemPartNote {
			return self.notes.find((n) => { n.id === id })!;
		}
	}));

type MstItemWithNotesType = typeof MstItemPartNoteStoreModel;
export interface MstItemWithNotes extends MstItemWithNotesType { }
export const ItemWithNotesModel: MstItemWithNotes = MstItemPartNoteStoreModel;
export interface ItemWithNotes extends Instance<typeof ItemWithNotesModel> { }
export type MstItemWithNotesSnapshot = SnapshotIn<typeof MstItemPartNoteStoreModel>;
export interface ItemWithNotesSnapshot extends MstItemWithNotesSnapshot { }
