import { createFileRoute } from "@tanstack/react-router";
import { UserPage } from "../areas/user/ui/UserPage";

export const Route = createFileRoute("/user/$userId")({
	component: UserPageRoute,
});

function UserPageRoute() {
	const { userId } = Route.useParams();
	return <UserPage userId={userId} />;
}
