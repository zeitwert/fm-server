import { createFileRoute } from "@tanstack/react-router";
import { Card, Typography } from "antd";
import { getArea } from "../app/config/AppConfig";

const { Title, Text } = Typography;

export const Route = createFileRoute("/portfolio")({
	component: PortfolioArea,
});

function PortfolioArea() {
	return (
		<Card>
			<Title level={3}>
				<span style={{ marginRight: 12 }}>{getArea("portfolio")?.icon}</span>
				Portfolios
			</Title>
			<Text type="secondary">
				Verwalten Sie Ihre Immobilienportfolios. Erstellen Sie neue Portfolios oder bearbeiten Sie
				bestehende.
			</Text>
		</Card>
	);
}
