import { createFileRoute } from "@tanstack/react-router";
import { PortfolioArea } from "@/areas/portfolio/ui/PortfolioArea";

export const Route = createFileRoute("/portfolio/")({
	component: PortfolioArea,
});
