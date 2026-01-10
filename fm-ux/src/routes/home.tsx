import { createFileRoute } from '@tanstack/react-router';
import { Card, Typography } from 'antd';
import { DashboardOutlined } from '@ant-design/icons';

const { Title, Text } = Typography;

export const Route = createFileRoute('/home')({
	component: HomeArea,
});

function HomeArea() {
	return (
		<Card>
			<Title level={3}>
				<DashboardOutlined style={{ marginRight: 12 }} />
				Dashboard
			</Title>
			<Text type="secondary">
				Willkommen im Dashboard. Hier finden Sie eine Übersicht über Ihre wichtigsten Kennzahlen und
				Aktivitäten.
			</Text>
		</Card>
	);
}
