import { MenuFoldOutlined, MenuUnfoldOutlined } from '@ant-design/icons';
import { Link, useLocation } from '@tanstack/react-router';
import { Button, Flex, Menu, theme } from 'antd';
import type { MenuProps } from 'antd';
import { useMemo } from 'react';
import { useTranslation } from 'react-i18next';
import { getApplicationInfo } from '../app/config/AppConfig';
import { useSessionStore } from '../session/model/sessionStore';
import { useShellStore } from './shellStore';

const { useToken } = theme;

type MenuItem = Required<MenuProps>['items'][number];

export function AppSidebar() {
	const { t } = useTranslation('app');
	const { token } = useToken();
	const { sidebarCollapsed, toggleSidebar } = useShellStore();
	const { sessionInfo } = useSessionStore();
	const location = useLocation();

	// Get current application areas
	const appInfo = useMemo(() => {
		if (!sessionInfo?.applicationId) return null;
		return getApplicationInfo(sessionInfo.applicationId);
	}, [sessionInfo?.applicationId]);

	// Build menu items from application areas
	const navigationItems: MenuItem[] = useMemo(() => {
		if (!appInfo) return [];

		return appInfo.areas.map((area) => ({
			key: area.path,
			icon: area.icon,
			label: <Link to={`/${area.path}` as '/'}>{area.label(t)}</Link>,
		}));
	}, [appInfo, t]);

	// Get current selected key from URL path
	const selectedKeys = useMemo(() => {
		const path = location.pathname.replace('/', '');
		return path ? [path] : [appInfo?.defaultArea ?? 'home'];
	}, [location.pathname, appInfo?.defaultArea]);

	return (
		<Flex
			vertical
			justify="space-between"
			style={{
				height: '100%',
				background: token.colorBgContainer,
				borderRight: `1px solid ${token.colorBorderSecondary}`,
			}}
		>
			{/* Navigation Menu */}
			<Menu
				mode="inline"
				selectedKeys={selectedKeys}
				items={navigationItems}
				inlineCollapsed={sidebarCollapsed}
				style={{
					border: 'none',
					background: 'transparent',
				}}
			/>

			{/* Collapse Toggle */}
			<Flex
				justify="center"
				style={{
					padding: '12px 0',
					borderTop: `1px solid ${token.colorBorderSecondary}`,
				}}
			>
				<Button
					type="text"
					icon={sidebarCollapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
					onClick={toggleSidebar}
					style={{
						width: sidebarCollapsed ? 40 : '100%',
						margin: sidebarCollapsed ? 0 : '0 8px',
					}}
					aria-label="app:collapse"
				>
					{!sidebarCollapsed && t('collapse')}
				</Button>
			</Flex>
		</Flex>
	);
}
