import { createFileRoute } from "@tanstack/react-router";
import { NoteArea } from "../areas/note/ui/NoteArea";

export const Route = createFileRoute("/note/")({
	component: NoteArea,
});
