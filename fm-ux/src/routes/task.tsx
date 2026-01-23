import { createFileRoute, Outlet } from "@tanstack/react-router";

export const Route = createFileRoute("/task")({
	component: TaskLayout,
});

function TaskLayout() {
	return <Outlet />;
}
