import { createFileRoute } from "@tanstack/react-router";
import { Card, Typography } from "antd";
import { HomeOutlined } from "@ant-design/icons";

const { Title, Text } = Typography;

export const Route = createFileRoute("/building")({
	component: BuildingArea,
});

function BuildingArea() {
	return (
		<Card>
			<Title level={3}>
				<HomeOutlined style={{ marginRight: 12 }} />
				Immobilien
			</Title>
			<Text type="secondary">
				Übersicht aller Immobilien in Ihrem Bestand. Erfassen und verwalten Sie Gebäudedaten,
				Zustandsbewertungen und Massnahmen.
			</Text>
		</Card>
	);
}
