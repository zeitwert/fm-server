import { createFileRoute } from '@tanstack/react-router';
import { Card, Typography } from 'antd';
import { FileTextOutlined } from '@ant-design/icons';

const { Title, Text } = Typography;

export const Route = createFileRoute('/tenant')({
	component: TenantArea,
});

function TenantArea() {
	return (
		<Card>
			<Title level={3}>
				<FileTextOutlined style={{ marginRight: 12 }} />
				Mandanten
			</Title>
			<Text type="secondary">
				Mandantenverwaltung für Administratoren. Konfigurieren Sie Mandanteneinstellungen und
				Berechtigungen.
			</Text>
		</Card>
	);
}
