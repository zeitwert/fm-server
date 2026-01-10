import { createFileRoute } from '@tanstack/react-router';
import { Card, Typography } from 'antd';
import { CheckSquareOutlined } from '@ant-design/icons';

const { Title, Text } = Typography;

export const Route = createFileRoute('/task')({
	component: TaskArea,
});

function TaskArea() {
	return (
		<Card>
			<Title level={3}>
				<CheckSquareOutlined style={{ marginRight: 12 }} />
				Aufgaben
			</Title>
			<Text type="secondary">
				Verwalten Sie Ihre Aufgaben und Termine. Behalten Sie den Überblick über anstehende und
				abgeschlossene Arbeiten.
			</Text>
		</Card>
	);
}
