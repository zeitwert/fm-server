import { createFileRoute } from "@tanstack/react-router";
import { AccountPage } from "@/areas/account/ui/AccountPage";

export const Route = createFileRoute("/account/$accountId")({
	component: AccountPageRoute,
});

function AccountPageRoute() {
	const { accountId } = Route.useParams();
	return <AccountPage accountId={accountId} />;
}
