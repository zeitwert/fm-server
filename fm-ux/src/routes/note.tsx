import { createFileRoute, Outlet } from "@tanstack/react-router";

export const Route = createFileRoute("/note")({
	component: NoteLayout,
});

function NoteLayout() {
	return <Outlet />;
}
