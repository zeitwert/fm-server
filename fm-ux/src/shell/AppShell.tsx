import { Outlet } from '@tanstack/react-router';
import { Layout, theme } from 'antd';
import { AppHeader } from './AppHeader';
import { AppSidebar } from './AppSidebar';
import { useShellStore } from './shellStore';

const { Content, Sider } = Layout;
const { useToken } = theme;

const SIDEBAR_WIDTH = 220;
const SIDEBAR_COLLAPSED_WIDTH = 80;

export function AppShell() {
	const { token } = useToken();
	const { sidebarCollapsed } = useShellStore();

	return (
		<Layout style={{ minHeight: '100vh' }}>
			{/* Header */}
			<Layout.Header
				style={{
					padding: 0,
					height: 48,
					lineHeight: '48px',
					background: token.colorBgContainer,
					position: 'fixed',
					top: 0,
					left: 0,
					right: 0,
					zIndex: 100,
				}}
			>
				<AppHeader />
			</Layout.Header>

			{/* Main Layout */}
			<Layout style={{ marginTop: 48 }}>
				{/* Sidebar */}
				<Sider
					width={SIDEBAR_WIDTH}
					collapsedWidth={SIDEBAR_COLLAPSED_WIDTH}
					collapsed={sidebarCollapsed}
					trigger={null}
					style={{
						background: token.colorBgContainer,
						position: 'fixed',
						top: 48,
						left: 0,
						bottom: 0,
						overflow: 'auto',
					}}
				>
					<AppSidebar />
				</Sider>

				{/* Content Area */}
				<Layout
					style={{
						marginLeft: sidebarCollapsed ? SIDEBAR_COLLAPSED_WIDTH : SIDEBAR_WIDTH,
						transition: 'margin-left 0.2s',
					}}
				>
					<Content
						style={{
							padding: 24,
							background: token.colorBgLayout,
							minHeight: 'calc(100vh - 48px)',
						}}
					>
						<Outlet />
					</Content>
				</Layout>
			</Layout>
		</Layout>
	);
}

