import { Outlet } from "@tanstack/react-router";
import { Layout, theme } from "antd";
import { useEffect } from "react";
import { AppHeader } from "./AppHeader";
import { AppSidebar } from "./AppSidebar";
import { useShellStore } from "./shellStore";

const { Sider } = Layout;
const { useToken } = theme;

const SIDEBAR_WIDTH = 220;
const SIDEBAR_COLLAPSED_WIDTH = 80;

export function AppShell() {
	const { token } = useToken();
	const { sidebarCollapsed } = useShellStore();

	useEffect(() => {
		const previousBodyOverflow = document.body.style.overflow;
		const previousHtmlOverflow = document.documentElement.style.overflow;

		document.body.style.overflow = "hidden";
		document.documentElement.style.overflow = "hidden";

		return () => {
			document.body.style.overflow = previousBodyOverflow;
			document.documentElement.style.overflow = previousHtmlOverflow;
		};
	}, []);

	return (
		<Layout style={{ height: "100vh", maxHeight: "100vh", overflow: "hidden" }}>
			{/* Header */}
			<Layout.Header
				style={{
					padding: 0,
					height: 48,
					lineHeight: "normal",
					background: token.colorBgContainer,
					position: "fixed",
					top: 0,
					left: 0,
					right: 0,
					zIndex: 100,
					flex: "none",
					display: "flex",
					alignItems: "center",
				}}
			>
				<AppHeader />
			</Layout.Header>

			{/* Main Layout */}
			<Layout style={{ marginTop: 48, flex: 1, minHeight: 0, overflow: "hidden" }}>
				{/* Sidebar */}
				<Sider
					width={SIDEBAR_WIDTH}
					collapsedWidth={SIDEBAR_COLLAPSED_WIDTH}
					collapsed={sidebarCollapsed}
					trigger={null}
					style={{
						background: token.colorBgContainer,
						position: "fixed",
						top: 48,
						left: 0,
						bottom: 0,
						overflow: "auto",
					}}
				>
					<AppSidebar />
				</Sider>

				{/* Content Area */}
				<div
					style={{
						position: "absolute",
						top: 48,
						left: sidebarCollapsed ? SIDEBAR_COLLAPSED_WIDTH : SIDEBAR_WIDTH,
						right: 0,
						bottom: 0,
						padding: 16,
						boxSizing: "border-box",
						background: token.colorBgContainer,
						overflow: "hidden",
						transition: "left 0.2s",
					}}
				>
					<Outlet />
				</div>
			</Layout>
		</Layout>
	);
}
