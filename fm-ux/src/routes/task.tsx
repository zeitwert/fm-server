import { createFileRoute } from "@tanstack/react-router";
import { Card, Typography } from "antd";
import { getArea } from "../app/config/AppConfig";

const { Title, Text } = Typography;

export const Route = createFileRoute("/task")({
	component: TaskArea,
});

function TaskArea() {
	return (
		<Card>
			<Title level={3}>
				<span style={{ marginRight: 12 }}>{getArea("task")?.icon}</span>
				Aufgaben
			</Title>
			<Text type="secondary">
				Verwalten Sie Ihre Aufgaben und Termine. Behalten Sie den Überblick über anstehende und
				abgeschlossene Arbeiten.
			</Text>
		</Card>
	);
}
