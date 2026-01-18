import {
	BankOutlined,
	DownOutlined,
	LogoutOutlined,
	SearchOutlined,
	SettingOutlined,
	UserOutlined,
} from '@ant-design/icons';
import { Avatar, Dropdown, Flex, Input, Space, theme, Typography } from 'antd';
import type { MenuProps } from 'antd';
import { useEffect, useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { getLogoUrl } from '../common/api/client';
import { useSessionStore } from '../session/model/sessionStore';

const { Text } = Typography;
const { useToken } = theme;

export function AppHeader() {
	const { t } = useTranslation('common');
	const { token } = useToken();
	const { sessionInfo, tenantInfo, switchAccount, logout } = useSessionStore();
	const [logoError, setLogoError] = useState(false);

	const currentAccount = sessionInfo?.account;
	const availableAccounts = tenantInfo?.accounts ?? [];
	const hasMultipleAccounts = availableAccounts.length > 1;

	// Derive logo URL from session - prioritize account logo over tenant logo
	// Always try to load the logo if we have an ID - the error handler will catch 404s
	// Note: The logo relationship data may not be included in the session JSON response,
	// so we just check for account/tenant existence rather than logo data
	const logoUrl = useMemo(() => {
		if (sessionInfo?.account?.id) {
			return getLogoUrl('account', sessionInfo.account.id);
		}
		if (sessionInfo?.tenant?.id) {
			return getLogoUrl('tenant', sessionInfo.tenant.id);
		}
		return null;
	}, [sessionInfo?.account?.id, sessionInfo?.tenant?.id]);

	// Reset logo error state when the logo URL changes (e.g., account switch)
	useEffect(() => {
		setLogoError(false);
	}, [logoUrl]);

	// Title and subtitle following fm-ui pattern:
	// With account: title = account caption, subtitle = tenant caption
	// Without account: title = tenant caption, subtitle = tenant type name
	const title = sessionInfo?.account?.caption ?? sessionInfo?.tenant?.caption ?? 'FM-UX';
	const subTitle = sessionInfo?.account
		? sessionInfo?.tenant?.caption
		: sessionInfo?.tenant?.tenantType?.name;

	const accountMenuItems: MenuProps['items'] = availableAccounts.map((account) => ({
		key: account.id,
		label: account.name,
		icon: <BankOutlined />,
		onClick: () => {
			if (account.id !== currentAccount?.id) {
				switchAccount(account.id);
			}
		},
	}));

	const userMenuItems: MenuProps['items'] = [
		{
			key: 'settings',
			label: t('settings'),
			icon: <SettingOutlined />,
		},
		{
			type: 'divider',
		},
		{
			key: 'logout',
			label: t('logout'),
			icon: <LogoutOutlined />,
			danger: true,
			onClick: () => logout(),
		},
	];

	return (
		<div
			style={{
				display: 'grid',
				gridTemplateColumns: '1fr auto 1fr',
				alignItems: 'center',
				width: '100%',
				height: 48,
				padding: '0 16px',
				boxSizing: 'border-box',
				background: token.colorBgContainer,
				borderBottom: `1px solid ${token.colorBorderSecondary}`,
			}}
		>
			{/* Left: Logo and App Name */}
			<Flex align="center" gap={6} style={{ justifySelf: 'start' }}>
				{logoUrl && !logoError ? (
					<img
						src={logoUrl}
						alt="Logo"
						style={{
							width: 32,
							height: 32,
							borderRadius: 6,
							objectFit: 'contain',
						}}
						onError={() => setLogoError(true)}
					/>
				) : (
					<div
						style={{
							width: 32,
							height: 32,
							borderRadius: 6,
							background: `linear-gradient(135deg, ${token.colorPrimary} 0%, ${token.colorPrimaryActive} 100%)`,
							display: 'flex',
							alignItems: 'center',
							justifyContent: 'center',
						}}
					>
						<BankOutlined style={{ color: '#fff', fontSize: 18 }} />
					</div>
				)}
				<Flex vertical gap={-2} style={{ lineHeight: 1.1 }}>
					<Text strong style={{ fontSize: 15, letterSpacing: -0.3 }}>
						{title}
					</Text>
					{subTitle && (
						<Text style={{ fontSize: 10, color: token.colorTextSecondary, marginTop: -4 }}>
							{subTitle}
						</Text>
					)}
				</Flex>
			</Flex>

			{/* Center: Search */}
			<div style={{ width: 400 }}>
				<Input
					prefix={<SearchOutlined style={{ color: token.colorTextPlaceholder }} />}
					placeholder={t('searchPlaceholder')}
					size="middle"
					style={{
						borderRadius: 20,
						background: token.colorFillTertiary,
					}}
					variant="filled"
					aria-label="common:search"
				/>
			</div>

			{/* Right: Account, User, Settings */}
			<Flex align="center" gap={16} style={{ justifySelf: 'end' }}>
				{/* Account Chooser */}
				{currentAccount && (
					<>
						{hasMultipleAccounts ? (
							<Dropdown menu={{ items: accountMenuItems }} trigger={['click']}>
								<Space
									style={{
										cursor: 'pointer',
										padding: '4px 8px',
										borderRadius: 6,
										transition: 'background 0.2s',
									}}
									className="header-dropdown-trigger"
								>
									<BankOutlined style={{ color: token.colorTextSecondary }} />
									<Text style={{ maxWidth: 150, overflow: 'hidden', textOverflow: 'ellipsis' }}>
										{currentAccount.name}
									</Text>
									<DownOutlined style={{ fontSize: 10, color: token.colorTextSecondary }} />
								</Space>
							</Dropdown>
						) : (
							<Space style={{ padding: '4px 8px' }}>
								<BankOutlined style={{ color: token.colorTextSecondary }} />
								<Text style={{ maxWidth: 150, overflow: 'hidden', textOverflow: 'ellipsis' }}>
									{currentAccount.name}
								</Text>
							</Space>
						)}
					</>
				)}

				{/* User Menu */}
				<Dropdown menu={{ items: userMenuItems }} trigger={['click']} placement="bottomRight">
					<Space
						style={{
							cursor: 'pointer',
							padding: '4px 8px',
							borderRadius: 6,
							transition: 'background 0.2s',
						}}
						className="header-dropdown-trigger"
						aria-label="common:user"
					>
						<Avatar size={28} icon={<UserOutlined />} style={{ background: token.colorPrimary }} />
						<Text style={{ maxWidth: 120, overflow: 'hidden', textOverflow: 'ellipsis' }}>
							{sessionInfo?.user?.name ?? t('user')}
						</Text>
						<DownOutlined style={{ fontSize: 10, color: token.colorTextSecondary }} />
					</Space>
				</Dropdown>
			</Flex>
		</div>
	);
}
