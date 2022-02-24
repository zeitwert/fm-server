
import { session, UUID } from "@zeitwert/ui-model";
import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { ItemPartNote, ItemPartNoteModel, ItemPartNotePayload } from "./ItemPartNoteModel";

const MstItemWithNotesModel = types.model("ItemWithNotes")
	.props({
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
	}));

type MstItemWithNotesType = typeof MstItemWithNotesModel;
export interface MstItemWithNotes extends MstItemWithNotesType { }
export const ItemWithNotesModel: MstItemWithNotes = MstItemWithNotesModel;
export interface ItemWithNotes extends Instance<typeof ItemWithNotesModel> { }
export type MstItemWithNotesSnapshot = SnapshotIn<typeof MstItemWithNotesModel>;
export interface ItemWithNotesSnapshot extends MstItemWithNotesSnapshot { }
