import { createEntityApi } from "../../common/api/entityApi";
import type { Note, NoteListItem } from "./types";

export const noteApi = createEntityApi<Note>({
	module: "collaboration",
	path: "notes",
	type: "note",
	includes: "",
	relations: {},
});

export const noteListApi = createEntityApi<NoteListItem>({
	module: "collaboration",
	path: "notes",
	type: "note",
	includes: "",
	relations: {},
});
