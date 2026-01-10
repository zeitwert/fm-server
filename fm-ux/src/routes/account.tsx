import { createFileRoute } from '@tanstack/react-router';
import { Card, Typography } from 'antd';
import { BankOutlined } from '@ant-design/icons';

const { Title, Text } = Typography;

export const Route = createFileRoute('/account')({
	component: AccountArea,
});

function AccountArea() {
	return (
		<Card>
			<Title level={3}>
				<BankOutlined style={{ marginRight: 12 }} />
				Kunden
			</Title>
			<Text type="secondary">
				Kundenverwaltung und -übersicht. Verwalten Sie Ihre Kundenbeziehungen und Kontodaten.
			</Text>
		</Card>
	);
}
