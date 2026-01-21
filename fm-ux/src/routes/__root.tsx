import { Outlet, createRootRoute, useLocation } from "@tanstack/react-router";
import { ConfigProvider, Spin } from "antd";
import { useEffect } from "react";
import { appTheme } from "../app/theme";
import { useSessionStore } from "../session/model/sessionStore";
import { SessionState } from "../session/model/types";
import { AppShell } from "../shell/AppShell";

export const Route = createRootRoute({
	component: RootComponent,
});

function RootComponent() {
	const { state, initSession, needsTenantSelection, needsAccountSelection } = useSessionStore();
	const location = useLocation();

	const isLoginRoute = location.pathname === "/login";
	const isFullyAuthenticated =
		state === SessionState.open ||
		(state === SessionState.authenticated && !needsTenantSelection() && !needsAccountSelection());

	// Initialize session on mount
	useEffect(() => {
		initSession();
	}, [initSession]);

	// Redirect to login if not authenticated and not already on login page
	useEffect(() => {
		if (!isLoginRoute && !isFullyAuthenticated && state !== SessionState.pendingOpen) {
			// Don't redirect during initial session check or pending states
			if (state === SessionState.close) {
				// Use window.location for reliable path string
				const currentPath = window.location.pathname + window.location.search;
				const redirectParam =
					currentPath && currentPath !== "/" ? `?redirect=${encodeURIComponent(currentPath)}` : "";
				window.location.replace(`/login${redirectParam}`);
			}
		}
	}, [isLoginRoute, isFullyAuthenticated, state]);

	// On login route, just render the outlet (LoginComponent handles everything)
	if (isLoginRoute) {
		return (
			<ConfigProvider theme={appTheme}>
				<Outlet />
			</ConfigProvider>
		);
	}

	// Show loading during session initialization
	if (state === SessionState.pendingOpen) {
		return (
			<ConfigProvider theme={appTheme}>
				<div className="af-loading-container">
					<Spin size="large" />
				</div>
			</ConfigProvider>
		);
	}

	// If not authenticated and not on login route, show loading while redirect happens
	if (!isFullyAuthenticated) {
		return (
			<ConfigProvider theme={appTheme}>
				<div className="af-loading-container">
					<Spin size="large" />
				</div>
			</ConfigProvider>
		);
	}

	// Show main application when session is open
	return (
		<ConfigProvider theme={appTheme}>
			<AppShell />
		</ConfigProvider>
	);
}
