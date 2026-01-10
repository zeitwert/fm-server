import { createFileRoute } from '@tanstack/react-router';
import { Card, Typography } from 'antd';
import { FileTextOutlined } from '@ant-design/icons';

const { Title, Text } = Typography;

export const Route = createFileRoute('/document')({
	component: DocumentArea,
});

function DocumentArea() {
	return (
		<Card>
			<Title level={3}>
				<FileTextOutlined style={{ marginRight: 12 }} />
				Dokumente
			</Title>
			<Text type="secondary">
				Dokumentenverwaltung. Laden Sie Dokumente hoch, organisieren und durchsuchen Sie Ihre
				Ablage.
			</Text>
		</Card>
	);
}
