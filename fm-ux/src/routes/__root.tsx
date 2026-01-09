import { createRootRoute, Outlet } from '@tanstack/react-router';
import { ConfigProvider, Layout, Typography } from 'antd';

const { Content } = Layout;
const { Title, Text } = Typography;

export const Route = createRootRoute({
	component: RootComponent,
});

function RootComponent() {
	return (
		<ConfigProvider>
			<Layout style={{ minHeight: '100vh' }}>
				<Content
					style={{
						display: 'flex',
						justifyContent: 'center',
						alignItems: 'center',
						flexDirection: 'column',
						padding: '50px',
					}}
				>
					<Title>Hello World</Title>
					<Text type="secondary">FM-UX is running successfully!</Text>
					<Outlet />
				</Content>
			</Layout>
		</ConfigProvider>
	);
}
