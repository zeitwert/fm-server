import { createFileRoute } from "@tanstack/react-router";
import { UserArea } from "@/areas/user/ui/UserArea";

export const Route = createFileRoute("/user/")({
	component: UserArea,
});
