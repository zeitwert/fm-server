import { describe, it, expect, beforeEach, vi } from "vitest";
import { renderApp, screen, waitFor } from "@/test/utils";
import { server } from "@/test/mocks/server";
import { http, HttpResponse } from "msw";
import { multiTenantUser, multiAccountTenantInfo, singleTenantUser } from "@/test/mocks/fixtures";
import { useSessionStore } from "../model/sessionStore";
import { SessionState } from "../model/types";

// Timeout for async operations - can be increased if tests become flaky
const ASYNC_TIMEOUT = 500;

// Test timeout should be higher than individual async timeouts
vi.setConfig({ testTimeout: 5000 });

describe("Login Sequence", () => {
	beforeEach(() => {
		// Reset session store state
		useSessionStore.setState({
			state: SessionState.close,
			error: null,
			userInfo: null,
			selectedTenant: null,
			tenantInfo: null,
			selectedAccount: null,
			sessionInfo: null,
		});
	});

	describe("Single tenant, single account (auto-complete flow)", () => {
		it("should login and redirect to home automatically", async () => {
			const { user } = renderApp({ initialPath: "/login" });

			// Wait for login page to render - use aria-label for deterministic testing
			const emailInput = await screen.findByRole(
				"textbox",
				{ name: "login:email" },
				{ timeout: ASYNC_TIMEOUT }
			);
			expect(emailInput).toBeInTheDocument();

			const passwordInput = screen.getByLabelText("login:password");

			await user.type(emailInput, "test@example.com");
			await user.type(passwordInput, "password123");

			// Submit the form
			const submitButton = screen.getByRole("button", { name: "login:signIn" });
			await user.click(submitButton);

			// Wait for session to be open
			await waitFor(
				() => {
					const state = useSessionStore.getState();
					expect(state.state).toBe(SessionState.open);
					expect(state.sessionInfo).not.toBeNull();
				},
				{ timeout: ASYNC_TIMEOUT }
			);
		});
	});

	describe("Multiple tenants flow", () => {
		beforeEach(() => {
			// Override handler to return multi-tenant user
			server.use(
				http.post("/rest/session/authenticate", () => {
					return HttpResponse.json(multiTenantUser);
				})
			);
		});

		it("should show tenant selection wizard after authentication", async () => {
			const { user } = renderApp({ initialPath: "/login" });

			// Wait for login page
			const emailInput = await screen.findByRole(
				"textbox",
				{ name: "login:email" },
				{ timeout: ASYNC_TIMEOUT }
			);

			// Fill and submit credentials
			await user.type(emailInput, "multi@example.com");
			await user.type(screen.getByLabelText("login:password"), "password123");
			await user.click(screen.getByRole("button", { name: "login:signIn" }));

			// Wait for tenant selection wizard - title uses translation key
			const selectTenantText = await screen.findByText(
				"login:label.selectTenant",
				{},
				{ timeout: ASYNC_TIMEOUT }
			);
			expect(selectTenantText).toBeInTheDocument();

			// Both tenants should be visible
			expect(screen.getByText("Tenant Alpha")).toBeInTheDocument();
			expect(screen.getByText("Tenant Beta")).toBeInTheDocument();
		});

		it("should proceed to home after selecting tenant with single account", async () => {
			const { user } = renderApp({ initialPath: "/login" });

			// Login
			const emailInput = await screen.findByRole(
				"textbox",
				{ name: "login:email" },
				{ timeout: ASYNC_TIMEOUT }
			);

			await user.type(emailInput, "multi@example.com");
			await user.type(screen.getByLabelText("login:password"), "password123");
			await user.click(screen.getByRole("button", { name: "login:signIn" }));

			// Wait for tenant selection
			await screen.findByText("login:label.selectTenant", {}, { timeout: ASYNC_TIMEOUT });

			// Click on first tenant
			await user.click(screen.getByText("Tenant Alpha"));

			// Should complete login (single account auto-selected)
			await waitFor(
				() => {
					const state = useSessionStore.getState();
					expect(state.state).toBe(SessionState.open);
				},
				{ timeout: ASYNC_TIMEOUT }
			);
		});
	});

	describe("Multiple accounts flow", () => {
		beforeEach(() => {
			// Single tenant user, but tenant has multiple accounts
			server.use(
				http.post("/rest/session/authenticate", () => {
					return HttpResponse.json(singleTenantUser);
				}),
				http.get("/rest/app/tenantInfo/:tenantId", () => {
					return HttpResponse.json(multiAccountTenantInfo);
				})
			);
		});

		it("should show account selection after tenant is auto-selected", async () => {
			const { user } = renderApp({ initialPath: "/login" });

			// Login
			const emailInput = await screen.findByRole(
				"textbox",
				{ name: "login:email" },
				{ timeout: ASYNC_TIMEOUT }
			);

			await user.type(emailInput, "test@example.com");
			await user.type(screen.getByLabelText("login:password"), "password123");
			await user.click(screen.getByRole("button", { name: "login:signIn" }));

			// Wait for account selection wizard (tenant auto-selected since only one)
			await screen.findByText("login:label.selectAccount", {}, { timeout: ASYNC_TIMEOUT });

			// Both accounts should be visible
			expect(screen.getByText("Account One")).toBeInTheDocument();
			expect(screen.getByText("Account Two")).toBeInTheDocument();
		});

		it("should complete login after selecting account", async () => {
			const { user } = renderApp({ initialPath: "/login" });

			// Login
			const emailInput = await screen.findByRole(
				"textbox",
				{ name: "login:email" },
				{ timeout: ASYNC_TIMEOUT }
			);

			await user.type(emailInput, "test@example.com");
			await user.type(screen.getByLabelText("login:password"), "password123");
			await user.click(screen.getByRole("button", { name: "login:signIn" }));

			// Wait for account selection
			await screen.findByText("login:label.selectAccount", {}, { timeout: ASYNC_TIMEOUT });

			// Select first account
			await user.click(screen.getByText("Account One"));

			// Should complete login
			await waitFor(
				() => {
					const state = useSessionStore.getState();
					expect(state.state).toBe(SessionState.open);
					expect(state.selectedAccount?.id).toBe("1000");
				},
				{ timeout: ASYNC_TIMEOUT }
			);
		});
	});

	describe("Full flow with multiple tenants and accounts", () => {
		beforeEach(() => {
			server.use(
				http.post("/rest/session/authenticate", () => {
					return HttpResponse.json(multiTenantUser);
				}),
				http.get("/rest/app/tenantInfo/:tenantId", () => {
					return HttpResponse.json(multiAccountTenantInfo);
				})
			);
		});

		it("should navigate through complete wizard flow", async () => {
			const { user } = renderApp({ initialPath: "/login" });

			// Login
			const emailInput = await screen.findByRole(
				"textbox",
				{ name: "login:email" },
				{ timeout: ASYNC_TIMEOUT }
			);

			await user.type(emailInput, "multi@example.com");
			await user.type(screen.getByLabelText("login:password"), "password123");
			await user.click(screen.getByRole("button", { name: "login:signIn" }));

			// Step 1: Tenant selection
			await screen.findByText("login:label.selectTenant", {}, { timeout: ASYNC_TIMEOUT });
			await user.click(screen.getByText("Tenant Alpha"));

			// Step 2: Account selection
			await screen.findByText("login:label.selectAccount", {}, { timeout: ASYNC_TIMEOUT });
			await user.click(screen.getByText("Account Two"));

			// Should complete login with selected account
			await waitFor(
				() => {
					const state = useSessionStore.getState();
					expect(state.state).toBe(SessionState.open);
					expect(state.selectedAccount?.id).toBe("2000");
				},
				{ timeout: ASYNC_TIMEOUT }
			);
		});

		it("should allow going back to tenant selection", async () => {
			const { user } = renderApp({ initialPath: "/login" });

			// Login and get to account selection
			const emailInput = await screen.findByRole(
				"textbox",
				{ name: "login:email" },
				{ timeout: ASYNC_TIMEOUT }
			);

			await user.type(emailInput, "multi@example.com");
			await user.type(screen.getByLabelText("login:password"), "password123");
			await user.click(screen.getByRole("button", { name: "login:signIn" }));

			await screen.findByText("login:label.selectTenant", {}, { timeout: ASYNC_TIMEOUT });
			await user.click(screen.getByText("Tenant Alpha"));

			await screen.findByText("login:label.selectAccount", {}, { timeout: ASYNC_TIMEOUT });

			// Click back button
			const backButton = screen.getByRole("button", { name: "login:backToTenant" });
			await user.click(backButton);

			// Should be back at tenant selection
			await screen.findByText("login:label.selectTenant", {}, { timeout: ASYNC_TIMEOUT });
		});
	});

	describe("Error handling", () => {
		it("should display error for invalid credentials", async () => {
			const { user } = renderApp({ initialPath: "/login" });

			const emailInput = await screen.findByRole(
				"textbox",
				{ name: "login:email" },
				{ timeout: ASYNC_TIMEOUT }
			);

			// Use password that triggers error in handler
			await user.type(emailInput, "test@example.com");
			await user.type(screen.getByLabelText("login:password"), "invalid");
			await user.click(screen.getByRole("button", { name: "login:signIn" }));

			// Should show error message
			await waitFor(
				() => {
					const state = useSessionStore.getState();
					expect(state.state).toBe(SessionState.close);
					expect(state.error).toBeTruthy();
				},
				{ timeout: ASYNC_TIMEOUT }
			);
		});

		it("should handle server errors", async () => {
			server.use(
				http.post("/rest/session/authenticate", () => {
					return HttpResponse.json({ errors: [{ detail: "Server error" }] }, { status: 500 });
				})
			);

			const { user } = renderApp({ initialPath: "/login" });

			const emailInput = await screen.findByRole(
				"textbox",
				{ name: "login:email" },
				{ timeout: ASYNC_TIMEOUT }
			);

			await user.type(emailInput, "test@example.com");
			await user.type(screen.getByLabelText("login:password"), "password123");
			await user.click(screen.getByRole("button", { name: "login:signIn" }));

			// Should remain in close state with error
			await waitFor(
				() => {
					const state = useSessionStore.getState();
					expect(state.state).toBe(SessionState.close);
				},
				{ timeout: ASYNC_TIMEOUT }
			);
		});
	});

	describe("Dashboard display after login", () => {
		it("should have correct session info after successful login", async () => {
			const { user } = renderApp({ initialPath: "/login" });

			// Login
			const emailInput = await screen.findByRole(
				"textbox",
				{ name: "login:email" },
				{ timeout: ASYNC_TIMEOUT }
			);

			await user.type(emailInput, "test@example.com");
			await user.type(screen.getByLabelText("login:password"), "password123");
			await user.click(screen.getByRole("button", { name: "login:signIn" }));

			// Wait for session to be open
			await waitFor(
				() => {
					const state = useSessionStore.getState();
					expect(state.state).toBe(SessionState.open);
				},
				{ timeout: ASYNC_TIMEOUT }
			);

			// Verify session info is correct for the redirect
			const state = useSessionStore.getState();
			expect(state.sessionInfo?.account?.name).toBe("Default Account");
			expect(state.sessionInfo?.tenant?.name).toBe("Test Tenant");
		});
	});
});
