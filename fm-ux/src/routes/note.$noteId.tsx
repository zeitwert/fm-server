import { createFileRoute } from "@tanstack/react-router";
import { NotePage } from "@/areas/note/ui/NotePage";

export const Route = createFileRoute("/note/$noteId")({
	component: NotePageRoute,
});

function NotePageRoute() {
	const { noteId } = Route.useParams();
	return <NotePage noteId={noteId} />;
}
