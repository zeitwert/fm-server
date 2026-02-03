import { BankOutlined } from "@ant-design/icons";
import { Flex, theme, Typography } from "antd";
import { useEffect, useMemo, useState } from "react";
import { getLogoUrl } from "@/common/api/client";
import { useSessionStore } from "@/session/model/sessionStore";

const { Text } = Typography;
const { useToken } = theme;

export function AppLogo() {
	const { token } = useToken();
	const { sessionInfo } = useSessionStore();
	const [logoError, setLogoError] = useState(false);

	// Derive logo URL from session - prioritize account logo over tenant logo
	// Always try to load the logo if we have an ID - the error handler will catch 404s
	// Note: The logo relationship data may not be included in the session JSON response,
	// so we just check for account/tenant existence rather than logo data
	const logoUrl = useMemo(() => {
		if (sessionInfo?.account?.id) {
			return getLogoUrl("account", sessionInfo.account.id);
		}
		if (sessionInfo?.tenant?.id) {
			return getLogoUrl("tenant", sessionInfo.tenant.id);
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
	const title = sessionInfo?.account?.caption ?? sessionInfo?.tenant?.caption ?? "FM-UX";
	const subTitle = sessionInfo?.account
		? sessionInfo?.tenant?.caption
		: sessionInfo?.tenant?.tenantType?.name;

	return (
		<Flex align="center" gap={6} style={{ justifySelf: "start" }}>
			{logoUrl && !logoError ? (
				<img
					src={logoUrl}
					alt="Logo"
					style={{
						width: 32,
						height: 32,
						borderRadius: 6,
						objectFit: "contain",
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
						display: "flex",
						alignItems: "center",
						justifyContent: "center",
					}}
				>
					<BankOutlined style={{ color: "#fff", fontSize: 18 }} />
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
	);
}
