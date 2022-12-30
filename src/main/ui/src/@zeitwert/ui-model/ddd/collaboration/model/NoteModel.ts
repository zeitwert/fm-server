
import { Instance, SnapshotIn, types } from "mobx-state-tree";
import { Enumerated } from "../../aggregate/model/EnumeratedModel";
import { ObjModel } from "../../obj/model/ObjModel";

export const NOTE: Enumerated = { id: "note", name: "Notiz" };
export const CALL: Enumerated = { id: "call", name: "Gesprächsnotiz" };
export const VISIT: Enumerated = { id: "visit", name: "Besuchsnotiz" };

const MstNoteModel = ObjModel.named("Note")
	.props({
		relatedToId: types.string,
		noteType: types.frozen<Enumerated>(),
		isPrivate: types.boolean,
		subject: types.maybe(types.string),
		content: types.maybe(types.string),
	})
	.views((self) => ({
		get isNote() {
			return self.noteType.id === "note";
		},
		get isCall() {
			return self.noteType.id === "call";
		},
		get isVisit() {
			return self.noteType.id === "visit";
		},
	}))
	.actions((self) => ({
		update(note: any) {
			self.subject = note.subject;
			self.content = note.content;
			self.isPrivate = note.isPrivate;
		},
	}));

type MstNoteType = typeof MstNoteModel;
interface MstNote extends MstNoteType { }

export const NoteModel: MstNote = MstNoteModel;
export type NoteModelType = typeof NoteModel;
export interface Note extends Instance<NoteModelType> { }
export type NoteSnapshot = SnapshotIn<NoteModelType>;
export type NotePayload = Omit<NoteSnapshot, "id">;
