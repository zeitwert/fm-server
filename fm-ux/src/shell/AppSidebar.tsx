import { MenuFoldOutlined, MenuUnfoldOutlined } from "@ant-design/icons";
import { Link, useLocation } from "@tanstack/react-router";
import { Button, Flex, Menu, theme } from "antd";
import type { MenuProps } from "antd";
import { useEffect, useMemo, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { getApplicationInfo } from "@/app/config/AppConfig";
import { useSessionStore } from "@/session/model/sessionStore";
import { useShellStore } from "./shellStore";
import { AppSwitcher } from "./components/AppSwitcher";

const { useToken } = theme;

type MenuItem = Required<MenuProps>["items"][number];

export function AppSidebar() {
	const { t } = useTranslation();
	const { token } = useToken();
	const { sidebarCollapsed, toggleSidebar } = useShellStore();
	const { sessionInfo } = useSessionStore();
	const location = useLocation();

	// Track previous application ID and slide direction for animation
	const prevAppIdRef = useRef<string | null>(sessionInfo?.applicationId ?? null);
	const [slideDirection, setSlideDirection] = useState<"left" | "right" | null>(null);

	// Detect application changes and determine slide direction
	useEffect(() => {
		const currentAppId = sessionInfo?.applicationId;
		const prevAppId = prevAppIdRef.current;

		if (prevAppId && currentAppId && prevAppId !== currentAppId) {
			const apps = sessionInfo?.availableApplications ?? [];
			const prevIndex = apps.indexOf(prevAppId);
			const currentIndex = apps.indexOf(currentAppId);

			// Determine direction based on index change
			setSlideDirection(currentIndex > prevIndex ? "right" : "left");
		}

		prevAppIdRef.current = currentAppId ?? null;
	}, [sessionInfo?.applicationId, sessionInfo?.availableApplications]);

	// Reset slide direction after animation completes
	useEffect(() => {
		if (slideDirection) {
			// Parse the duration string (e.g., "0.2s") to milliseconds
			const durationMs = parseFloat(token.motionDurationMid) * 1000;
			const timer = setTimeout(() => setSlideDirection(null), durationMs);
			return () => clearTimeout(timer);
		}
	}, [slideDirection, token.motionDurationMid]);

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
			label: <Link to={`/${area.path}` as "/"}>{area.label(t)}</Link>,
		}));
	}, [appInfo, t]);

	// Get current selected key from URL path
	const selectedKeys = useMemo(() => {
		const path = location.pathname.replace("/", "");
		return path ? [path] : [appInfo?.defaultArea ?? "home"];
	}, [location.pathname, appInfo?.defaultArea]);

	return (
		<Flex
			vertical
			justify="space-between"
			style={{
				height: "100%",
				background: token.colorBgContainer,
				borderRight: `1px solid ${token.colorBorderSecondary}`,
			}}
		>
			<Flex vertical style={{ overflow: "hidden" }}>
				{/* Application Switcher - only show if multiple apps available */}
				<AppSwitcher collapsed={sidebarCollapsed} />

				{/* Navigation Menu with slide animation */}
				<div
					style={{
						animation: slideDirection
							? `slideInFrom${slideDirection === "right" ? "Right" : "Left"} ${token.motionDurationMid} ${token.motionEaseOut}`
							: undefined,
					}}
				>
					<Menu
						mode="inline"
						selectedKeys={selectedKeys}
						items={navigationItems}
						inlineCollapsed={sidebarCollapsed}
						style={{
							border: "none",
							background: "transparent",
						}}
					/>
				</div>
			</Flex>

			{/* Collapse Toggle */}
			<Flex
				justify="center"
				style={{
					padding: "12px 0",
					borderTop: `1px solid ${token.colorBorderSecondary}`,
				}}
			>
				<Button
					type="text"
					icon={sidebarCollapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
					onClick={toggleSidebar}
					style={{
						width: sidebarCollapsed ? 40 : "100%",
						margin: sidebarCollapsed ? 0 : "0 8px",
					}}
					aria-label="app:collapse"
				>
					{!sidebarCollapsed && t("app:action.collapse")}
				</Button>
			</Flex>
		</Flex>
	);
}
