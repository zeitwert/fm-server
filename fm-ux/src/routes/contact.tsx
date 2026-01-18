import { createFileRoute } from "@tanstack/react-router";
import { Card, Typography } from "antd";
import { TeamOutlined } from "@ant-design/icons";

const { Title, Text } = Typography;

export const Route = createFileRoute("/contact")({
	component: ContactArea,
});

function ContactArea() {
	return (
		<Card>
			<Title level={3}>
				<TeamOutlined style={{ marginRight: 12 }} />
				Kontakte
			</Title>
			<Text type="secondary">
				Kontaktverwaltung für alle Ihre Geschäftspartner, Dienstleister und Ansprechpersonen.
			</Text>
		</Card>
	);
}
