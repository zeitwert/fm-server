import { createFileRoute } from "@tanstack/react-router";
import { ContactPage } from "../areas/contact/ui/ContactPage";

export const Route = createFileRoute("/contact/$contactId")({
	component: ContactPageRoute,
});

function ContactPageRoute() {
	const { contactId } = Route.useParams();
	return <ContactPage contactId={contactId} />;
}
