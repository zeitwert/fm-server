import { createFileRoute } from "@tanstack/react-router";
import { Card, Typography } from "antd";
import { getArea } from "../app/config/AppConfig";

const { Title, Text } = Typography;

export const Route = createFileRoute("/tenant")({
	component: TenantArea,
});

function TenantArea() {
	return (
		<Card>
			<Title level={3}>
				<span style={{ marginRight: 12 }}>{getArea("tenant")?.icon}</span>
				Mandanten
			</Title>
			<Text type="secondary">
				Mandantenverwaltung für Administratoren. Konfigurieren Sie Mandanteneinstellungen und
				Berechtigungen.
			</Text>
		</Card>
	);
}
