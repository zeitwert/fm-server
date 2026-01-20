import { createFileRoute } from "@tanstack/react-router";
import { AccountArea } from "../areas/account/ui/AccountArea";

export const Route = createFileRoute("/account/")({
	component: AccountArea,
});
