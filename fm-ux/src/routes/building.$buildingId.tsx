import { createFileRoute } from "@tanstack/react-router";
import { BuildingPage } from "@/areas/building/ui/BuildingPage";

export const Route = createFileRoute("/building/$buildingId")({
	component: BuildingPageRoute,
});

function BuildingPageRoute() {
	const { buildingId } = Route.useParams();
	return <BuildingPage buildingId={buildingId} />;
}
