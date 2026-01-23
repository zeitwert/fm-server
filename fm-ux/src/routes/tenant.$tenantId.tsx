import { createFileRoute } from "@tanstack/react-router";
import { TenantPage } from "../areas/tenant/ui/TenantPage";

export const Route = createFileRoute("/tenant/$tenantId")({
	component: TenantPageRoute,
});

function TenantPageRoute() {
	const { tenantId } = Route.useParams();
	return <TenantPage tenantId={tenantId} />;
}
