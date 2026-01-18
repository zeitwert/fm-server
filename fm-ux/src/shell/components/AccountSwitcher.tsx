import { BankOutlined, DownOutlined } from "@ant-design/icons";
import { Dropdown, Space, theme, Typography } from "antd";
import type { MenuProps } from "antd";
import { useSessionStore } from "../../session/model/sessionStore";

const { Text } = Typography;
const { useToken } = theme;

export function AccountSwitcher() {
	const { token } = useToken();
	const { sessionInfo, tenantInfo, switchAccount } = useSessionStore();

	const currentAccount = sessionInfo?.account;
	const availableAccounts = tenantInfo?.accounts ?? [];
	const hasMultipleAccounts = availableAccounts.length > 1;

	const accountMenuItems: MenuProps["items"] = availableAccounts.map((account) => ({
		key: account.id,
		label: account.name,
		icon: <BankOutlined />,
		onClick: () => {
			if (account.id !== currentAccount?.id) {
				switchAccount(account.id);
			}
		},
	}));

	if (!currentAccount) {
		return null;
	}

	if (hasMultipleAccounts) {
		return (
			<Dropdown menu={{ items: accountMenuItems }} trigger={["click"]}>
				<Space
					style={{
						cursor: "pointer",
						padding: "4px 8px",
						borderRadius: 6,
						transition: "background 0.2s",
					}}
					className="header-dropdown-trigger"
				>
					<BankOutlined style={{ color: token.colorTextSecondary }} />
					<Text style={{ maxWidth: 150, overflow: "hidden", textOverflow: "ellipsis" }}>
						{currentAccount.name}
					</Text>
					<DownOutlined style={{ fontSize: 10, color: token.colorTextSecondary }} />
				</Space>
			</Dropdown>
		);
	}

	return (
		<Space style={{ padding: "4px 8px" }}>
			<BankOutlined style={{ color: token.colorTextSecondary }} />
			<Text style={{ maxWidth: 150, overflow: "hidden", textOverflow: "ellipsis" }}>
				{currentAccount.name}
			</Text>
		</Space>
	);
}
