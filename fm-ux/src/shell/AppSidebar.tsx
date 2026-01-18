import { MenuFoldOutlined, MenuUnfoldOutlined } from '@ant-design/icons';
import { Link, useLocation } from '@tanstack/react-router';
import { Button, Flex, Menu, Segmented, theme } from 'antd';
import type { MenuProps } from 'antd';
import { useMemo } from 'react';
import { useTranslation } from 'react-i18next';
import { ApplicationMap, getApplicationInfo } from '../app/config/AppConfig';
import { useSessionStore } from '../session/model/sessionStore';
import { useShellStore } from './shellStore';

const { useToken } = theme;

type MenuItem = Required<MenuProps>['items'][number];

export function AppSidebar() {
	const { t } = useTranslation('app');
	const { token } = useToken();
	const { sidebarCollapsed, toggleSidebar } = useShellStore();
	const { sessionInfo, switchApplication } = useSessionStore();
	const location = useLocation();

	// Get available applications for segmented control
	const availableApplications = sessionInfo?.availableApplications ?? [];
	const hasMultipleApps = availableApplications.length > 1;

	// Get current application for collapsed display
	const currentApp = sessionInfo?.applicationId ? ApplicationMap[sessionInfo.applicationId] : null;

	// Build segmented options from available applications
	const appSegmentOptions = useMemo(() => {
		return availableApplications
			.map((appId) => {
				const app = ApplicationMap[appId];
				if (!app) return null;
				return {
					value: appId,
					label: t(app.shortName),
				};
			})
			.filter((option): option is { value: string; label: string } => option !== null);
	}, [availableApplications, t]);

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
			<Flex vertical>
				{/* Application Switcher - only show if multiple apps available */}
				{hasMultipleApps && (
					<Flex
						justify="center"
						style={{
							padding: sidebarCollapsed ? '12px 0' : '12px 8px',
							borderBottom: `1px solid ${token.colorBorderSecondary}`,
						}}
					>
						{sidebarCollapsed ? (
							// Show two-letter app key when collapsed
							<div
								style={{
									width: 40,
									height: 32,
									display: 'flex',
									alignItems: 'center',
									justifyContent: 'center',
									fontWeight: 600,
									fontSize: 14,
									color: token.colorPrimary,
									background: token.colorPrimaryBg,
									borderRadius: token.borderRadius,
								}}
							>
								{currentApp?.appKey}
							</div>
						) : (
							<Segmented
								value={sessionInfo?.applicationId}
								options={appSegmentOptions}
								onChange={(value) => switchApplication(value as string)}
								block
								style={{ width: '100%' }}
							/>
						)}
					</Flex>
				)}

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
			</Flex>

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
