import { createFileRoute } from "@tanstack/react-router";
import { BuildingArea } from "@/areas/building/ui/BuildingArea";

export const Route = createFileRoute("/building/")({
	component: BuildingArea,
});
