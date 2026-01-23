import { createFileRoute } from "@tanstack/react-router";
import { TenantArea } from "../areas/tenant/ui/TenantArea";

export const Route = createFileRoute("/tenant/")({
	component: TenantArea,
});
