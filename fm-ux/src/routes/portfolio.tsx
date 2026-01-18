import { createFileRoute } from '@tanstack/react-router';
import { Card, Typography } from 'antd';
import { AppstoreOutlined } from '@ant-design/icons';

const { Title, Text } = Typography;

export const Route = createFileRoute('/portfolio')({
	component: PortfolioArea,
});

function PortfolioArea() {
	return (
		<Card>
			<Title level={3}>
				<AppstoreOutlined style={{ marginRight: 12 }} />
				Portfolios
			</Title>
			<Text type="secondary">
				Verwalten Sie Ihre Immobilienportfolios. Erstellen Sie neue Portfolios oder bearbeiten Sie
				bestehende.
			</Text>
		</Card>
	);
}
