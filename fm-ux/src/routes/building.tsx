import { createFileRoute, Outlet } from "@tanstack/react-router";

export const Route = createFileRoute("/building")({
	component: BuildingLayout,
});

function BuildingLayout() {
	return <Outlet />;
}
