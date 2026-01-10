import { createFileRoute } from '@tanstack/react-router';
import { Card, Typography } from 'antd';
import { UserOutlined } from '@ant-design/icons';

const { Title, Text } = Typography;

export const Route = createFileRoute('/user')({
	component: UserArea,
});

function UserArea() {
	return (
		<Card>
			<Title level={3}>
				<UserOutlined style={{ marginRight: 12 }} />
				Benutzer
			</Title>
			<Text type="secondary">
				Benutzerverwaltung. Erstellen und verwalten Sie Benutzerkonten und deren Berechtigungen.
			</Text>
		</Card>
	);
}
