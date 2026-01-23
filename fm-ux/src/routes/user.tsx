import { createFileRoute } from "@tanstack/react-router";
import { Card, Typography } from "antd";
import { getArea } from "../app/config/AppConfig";

const { Title, Text } = Typography;

export const Route = createFileRoute("/user")({
	component: UserArea,
});

function UserArea() {
	return (
		<Card>
			<Title level={3}>
				<span style={{ marginRight: 12 }}>{getArea("user")?.icon}</span>
				Benutzer
			</Title>
			<Text type="secondary">
				Benutzerverwaltung. Erstellen und verwalten Sie Benutzerkonten und deren Berechtigungen.
			</Text>
		</Card>
	);
}
