import { DownOutlined, UserOutlined } from "@ant-design/icons";
import { Avatar, Divider, Popover, Space, theme, Typography } from "antd";
import { useTranslation } from "react-i18next";
import { useSessionStore } from "../../session/model/sessionStore";

const { Text, Link } = Typography;
const { useToken } = theme;

function UserInfoContent() {
	const { t } = useTranslation();
	const { token } = useToken();
	const { sessionInfo, logout } = useSessionStore();

	const labelStyle: React.CSSProperties = {
		color: token.colorTextSecondary,
		minWidth: 80,
	};

	const valueStyle: React.CSSProperties = {
		color: token.colorText,
	};

	return (
		<div style={{ minWidth: 220 }}>
			{/* User info rows */}
			<div style={{ display: "flex", flexDirection: "column", gap: 4 }}>
				<div style={{ display: "flex" }}>
					<Text style={labelStyle}>{t("common:label.email")}:</Text>
					<Text style={valueStyle}>{sessionInfo?.user?.email}</Text>
				</div>
				<div style={{ display: "flex" }}>
					<Text style={labelStyle}>{t("common:label.tenant")}:</Text>
					<Text style={valueStyle}>{sessionInfo?.tenant?.caption}</Text>
				</div>
				<div style={{ display: "flex" }}>
					<Text style={labelStyle}>{t("common:label.account")}:</Text>
					<Text style={valueStyle}>
						{sessionInfo?.account?.name ?? t("common:label.noAccount")}
					</Text>
				</div>
			</div>

			<Divider style={{ margin: "12px 0" }} />

			{/* Action links */}
			<div style={{ display: "flex", gap: 16 }}>
				<Link>{t("common:label.settings")}</Link>
				<Link onClick={() => logout()}>{t("common:action.logout")}</Link>
			</div>

			<Divider style={{ margin: "12px 0" }} />

			{/* Application info */}
			<div style={{ display: "flex", flexDirection: "column", gap: 4 }}>
				<div style={{ display: "flex" }}>
					<Text style={labelStyle}>{t("application")}:</Text>
					<Text style={valueStyle}>{sessionInfo?.applicationName}</Text>
				</div>
				<div style={{ display: "flex" }}>
					<Text style={labelStyle}>{t("version")}:</Text>
					<Text style={valueStyle}>{sessionInfo?.applicationVersion}</Text>
				</div>
			</div>
		</div>
	);
}

export function UserInfo() {
	const { t } = useTranslation("common");
	const { token } = useToken();
	const { sessionInfo } = useSessionStore();

	const userName = sessionInfo?.user?.name ?? t("common:label.user");

	return (
		<Popover content={<UserInfoContent />} title={userName} trigger="click" placement="bottomRight">
			<Space
				style={{
					cursor: "pointer",
					padding: "4px 8px",
					borderRadius: 6,
					transition: "background 0.2s",
				}}
				className="header-dropdown-trigger"
				aria-label="common:user"
			>
				<Avatar size={28} icon={<UserOutlined />} style={{ background: token.colorPrimary }} />
				<Text style={{ maxWidth: 120, overflow: "hidden", textOverflow: "ellipsis" }}>
					{userName}
				</Text>
				<DownOutlined style={{ fontSize: 10, color: token.colorTextSecondary }} />
			</Space>
		</Popover>
	);
}
