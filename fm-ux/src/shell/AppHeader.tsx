import { Flex, theme } from "antd";
import { AccountSwitcher } from "./components/AccountSwitcher";
import { AppLogo } from "./components/AppLogo";
import { SearchBar } from "./components/SearchBar";
import { UserInfo } from "./components/UserInfo";

const { useToken } = theme;

export function AppHeader() {
	const { token } = useToken();

	return (
		<div
			style={{
				display: "grid",
				gridTemplateColumns: "1fr auto 1fr",
				alignItems: "center",
				width: "100%",
				height: 48,
				padding: "0 16px",
				boxSizing: "border-box",
				background: token.colorBgContainer,
				borderBottom: `1px solid ${token.colorBorderSecondary}`,
			}}
		>
			{/* Left: Logo and App Name */}
			<AppLogo />

			{/* Center: Search */}
			<SearchBar />

			{/* Right: Account, User */}
			<Flex align="center" gap={16} style={{ justifySelf: "end" }}>
				<AccountSwitcher />
				<UserInfo />
			</Flex>
		</div>
	);
}
