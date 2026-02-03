import { createFileRoute } from "@tanstack/react-router";
import { ContactArea } from "@/areas/contact/ui/ContactArea";

export const Route = createFileRoute("/contact/")({
	component: ContactArea,
});
