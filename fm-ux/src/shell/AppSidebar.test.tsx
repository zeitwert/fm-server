import { describe, it, expect, beforeEach, vi } from "vitest";
import { renderApp, screen, waitFor } from "../test/utils";
import {
	activeSessionInfo,
	superUserSessionInfo,
	singleAccountTenantInfo,
} from "../test/mocks/fixtures";
import { useSessionStore } from "../session/model/sessionStore";
import { SessionState } from "../session/model/types";
import { useShellStore } from "./shellStore";

// Increase timeout for integration tests
vi.setConfig({ testTimeout: 15000 });

// Mock Google Maps to avoid API calls in tests
vi.mock("google-map-react", () => ({
	default: ({ children }: { children: React.ReactNode }) => (
		<div data-testid="google-map">{children}</div>
	),
}));

describe("AppSidebar", () => {
	beforeEach(() => {
		// Reset shell store to default (expanded sidebar)
		useShellStore.setState({ sidebarCollapsed: false });
	});

	describe("App Switcher visibility", () => {
		it("should display app switcher when multiple applications are available", async () => {
			// Pre-populate session store with super user session (multiple apps)
			useSessionStore.setState({
				state: SessionState.open,
				sessionInfo: superUserSessionInfo,
				tenantInfo: singleAccountTenantInfo,
				selectedTenant: { id: "100", name: "Test Tenant" },
				selectedAccount: { id: "1000", name: "Default Account" },
				error: null,
				userInfo: null,
			});

			renderApp({ initialPath: "/home" });

			// Wait for the app switcher to appear
			await waitFor(
				() => {
					expect(screen.getByTestId("app-switcher")).toBeInTheDocument();
				},
				{ timeout: 10000 }
			);

			// Verify the segmented control is visible (expanded state)
			expect(screen.getByTestId("app-switcher-segmented")).toBeInTheDocument();

			// Verify both app options are available (translation keys)
			expect(screen.getByText("app:appFm")).toBeInTheDocument();
			expect(screen.getByText("app:appFmAdmin")).toBeInTheDocument();
		});

		it("should NOT display app switcher when only one application is available", async () => {
			// Pre-populate session store with regular user session (single app)
			useSessionStore.setState({
				state: SessionState.open,
				sessionInfo: activeSessionInfo,
				tenantInfo: singleAccountTenantInfo,
				selectedTenant: { id: "100", name: "Test Tenant" },
				selectedAccount: { id: "1000", name: "Default Account" },
				error: null,
				userInfo: null,
			});

			renderApp({ initialPath: "/home" });

			// Wait for the page to load
			await waitFor(
				() => {
					// Navigation menu should be present
					expect(screen.getByText("app:dashboard")).toBeInTheDocument();
				},
				{ timeout: 10000 }
			);

			// App switcher should NOT be present
			expect(screen.queryByTestId("app-switcher")).not.toBeInTheDocument();
		});
	});

	describe("App Switcher collapsed state", () => {
		it("should display app key badge when sidebar is collapsed", async () => {
			// Pre-populate session store with super user session (multiple apps)
			useSessionStore.setState({
				state: SessionState.open,
				sessionInfo: superUserSessionInfo,
				tenantInfo: singleAccountTenantInfo,
				selectedTenant: { id: "100", name: "Test Tenant" },
				selectedAccount: { id: "1000", name: "Default Account" },
				error: null,
				userInfo: null,
			});

			// Start with collapsed sidebar
			useShellStore.setState({ sidebarCollapsed: true });

			renderApp({ initialPath: "/home" });

			// Wait for the app switcher badge to appear
			await waitFor(
				() => {
					expect(screen.getByTestId("app-switcher")).toBeInTheDocument();
				},
				{ timeout: 10000 }
			);

			// Verify the badge is visible with the app key
			const badge = screen.getByTestId("app-switcher-badge");
			expect(badge).toBeInTheDocument();
			expect(badge).toHaveTextContent("FM"); // appKey for fm application

			// Segmented control should NOT be visible when collapsed
			expect(screen.queryByTestId("app-switcher-segmented")).not.toBeInTheDocument();
		});

		it("should toggle between segmented and badge when collapsing sidebar", async () => {
			// Pre-populate session store with super user session (multiple apps)
			useSessionStore.setState({
				state: SessionState.open,
				sessionInfo: superUserSessionInfo,
				tenantInfo: singleAccountTenantInfo,
				selectedTenant: { id: "100", name: "Test Tenant" },
				selectedAccount: { id: "1000", name: "Default Account" },
				error: null,
				userInfo: null,
			});

			const { user } = renderApp({ initialPath: "/home" });

			// Wait for app switcher in expanded state
			await waitFor(
				() => {
					expect(screen.getByTestId("app-switcher-segmented")).toBeInTheDocument();
				},
				{ timeout: 10000 }
			);

			// Click collapse button
			const collapseButton = screen.getByRole("button", { name: "app:collapse" });
			await user.click(collapseButton);

			// Should now show badge instead of segmented
			await waitFor(() => {
				expect(screen.getByTestId("app-switcher-badge")).toBeInTheDocument();
				expect(screen.queryByTestId("app-switcher-segmented")).not.toBeInTheDocument();
			});
		});
	});

	describe("Navigation menu", () => {
		it("should display navigation items for the current application", async () => {
			useSessionStore.setState({
				state: SessionState.open,
				sessionInfo: activeSessionInfo,
				tenantInfo: singleAccountTenantInfo,
				selectedTenant: { id: "100", name: "Test Tenant" },
				selectedAccount: { id: "1000", name: "Default Account" },
				error: null,
				userInfo: null,
			});

			renderApp({ initialPath: "/home" });

			// Wait for navigation menu to render
			await waitFor(
				() => {
					// fm app has: home, portfolio, building, task areas
					expect(screen.getByText("app:dashboard")).toBeInTheDocument();
					expect(screen.getByText("app:portfolios")).toBeInTheDocument();
					expect(screen.getByText("app:buildings")).toBeInTheDocument();
					expect(screen.getByText("app:tasks")).toBeInTheDocument();
				},
				{ timeout: 10000 }
			);
		});
	});
});
