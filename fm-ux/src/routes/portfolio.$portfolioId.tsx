import { createFileRoute } from "@tanstack/react-router";
import { PortfolioPage } from "../areas/portfolio/ui/PortfolioPage";

export const Route = createFileRoute("/portfolio/$portfolioId")({
	component: PortfolioPageRoute,
});

function PortfolioPageRoute() {
	const { portfolioId } = Route.useParams();
	return <PortfolioPage portfolioId={portfolioId} />;
}
