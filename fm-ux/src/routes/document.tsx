import { createFileRoute } from "@tanstack/react-router";
import { Card, Typography } from "antd";
import { getArea } from "@/app/config/AppConfig";

const { Title, Text } = Typography;

export const Route = createFileRoute("/document")({
	component: DocumentArea,
});

function DocumentArea() {
	return (
		<Card>
			<Title level={3}>
				<span style={{ marginRight: 12 }}>{getArea("document")?.icon}</span>
				Dokumente
			</Title>
			<Text type="secondary">
				Dokumentenverwaltung. Laden Sie Dokumente hoch, organisieren und durchsuchen Sie Ihre
				Ablage.
			</Text>
		</Card>
	);
}
