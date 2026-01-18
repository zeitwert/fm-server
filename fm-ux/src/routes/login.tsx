import { Navigate, createFileRoute, useSearch } from "@tanstack/react-router";
import { ConfigProvider, Spin } from "antd";
import { appTheme } from "../app/theme";
import { useSessionStore } from "../session/model/sessionStore";
import { SessionState } from "../session/model/types";
import { LoginPage } from "../session/ui/LoginPage";
import { SelectionWizard } from "../session/ui/SelectionWizard";

// Search params interface for redirect tracking
interface LoginSearchParams {
	redirect?: string;
}

export const Route = createFileRoute("/login")({
	validateSearch: (search: Record<string, unknown>): LoginSearchParams => {
		return {
			redirect: typeof search.redirect === "string" ? search.redirect : undefined,
		};
	},
	component: LoginComponent,
});

function LoginComponent() {
	const { state, needsTenantSelection, needsAccountSelection } = useSessionStore();
	const { redirect } = useSearch({ from: "/login" });

	// When session is open, redirect to intended destination or home
	if (state === SessionState.open) {
		const destination = redirect || "/home";
		return <Navigate to={destination as "/"} replace />;
	}

	// Show login page when not authenticated
	if (state === SessionState.close) {
		return (
			<ConfigProvider theme={appTheme}>
				<LoginPage />
			</ConfigProvider>
		);
	}

	// Show loading during authentication
	if (state === SessionState.pendingAuth) {
		return (
			<ConfigProvider theme={appTheme}>
				<div
					style={{
						minHeight: "100vh",
						display: "flex",
						justifyContent: "center",
						alignItems: "center",
						background: "#ffffff",
					}}
				>
					<Spin size="large" />
				</div>
			</ConfigProvider>
		);
	}

	// Show tenant/account selection wizard
	if (state === SessionState.authenticated && (needsTenantSelection() || needsAccountSelection())) {
		return (
			<ConfigProvider theme={appTheme}>
				<SelectionWizard />
			</ConfigProvider>
		);
	}

	// Show loading during session initialization
	if (state === SessionState.pendingOpen) {
		return (
			<ConfigProvider theme={appTheme}>
				<div
					style={{
						minHeight: "100vh",
						display: "flex",
						justifyContent: "center",
						alignItems: "center",
						background: "#ffffff",
					}}
				>
					<Spin size="large" />
				</div>
			</ConfigProvider>
		);
	}

	// Fallback - should not normally reach here
	return null;
}
