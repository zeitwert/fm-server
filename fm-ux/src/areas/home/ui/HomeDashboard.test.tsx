import { describe, it, expect, beforeEach, vi } from 'vitest';
import { renderApp, screen, waitFor } from '../../../test/utils';
import {
	activeSessionInfo,
	singleAccountTenantInfo,
} from '../../../test/mocks/fixtures';
import { useSessionStore } from '../../../session/model/sessionStore';
import { SessionState } from '../../../session/model/types';

// Increase timeout for integration tests
vi.setConfig({ testTimeout: 15000 });

// Mock Google Maps to avoid API calls and rendering issues in tests
vi.mock('google-map-react', () => ({
	default: ({ children }: { children: React.ReactNode }) => (
		<div data-testid="google-map">{children}</div>
	),
}));

describe('Home Dashboard', () => {
	beforeEach(() => {
		// Pre-populate session store to bypass login
		useSessionStore.setState({
			state: SessionState.open,
			sessionInfo: activeSessionInfo,
			tenantInfo: singleAccountTenantInfo,
			selectedTenant: { id: '100', name: 'Test Tenant' },
			selectedAccount: { id: '1000', name: 'Default Account' },
			error: null,
			userInfo: null,
		});
	});

	describe('Dashboard renders all cards', () => {
		it('should display all dashboard card titles', async () => {
			renderApp({ initialPath: '/home' });

			// Wait for the dashboard to load and verify all card titles
			await waitFor(
				() => {
					// Map card
					expect(screen.getByText('home:mapTitle')).toBeInTheDocument();
					// Statistics card
					expect(screen.getByText('home:statistics')).toBeInTheDocument();
					// Recent actions card
					expect(screen.getByText('home:recentActions')).toBeInTheDocument();
				},
				{ timeout: 10000 }
			);

			// Open activities card (includes count)
			await waitFor(
				() => {
					expect(screen.getByText(/home:openActivities/)).toBeInTheDocument();
				},
				{ timeout: 10000 }
			);

			// Overview card uses account name as title - appears multiple times (header + card)
			await waitFor(
				() => {
					const accountNames = screen.getAllByText('Default Account');
					expect(accountNames.length).toBeGreaterThanOrEqual(1);
				},
				{ timeout: 10000 }
			);
		});
	});

	describe('Overview card', () => {
		it('should display account statistics from fixture data', async () => {
			renderApp({ initialPath: '/home' });

			// Wait for overview data to load
			await waitFor(
				() => {
					// Building count from homeOverview fixture (25)
					expect(screen.getByText('25')).toBeInTheDocument();
				},
				{ timeout: 10000 }
			);

			// Portfolio count (5)
			expect(screen.getByText('5')).toBeInTheDocument();

			// Rating count (18)
			expect(screen.getByText('18')).toBeInTheDocument();

			// Verify labels are present (using translation keys)
			expect(screen.getByText('home:buildings')).toBeInTheDocument();
			expect(screen.getByText('home:portfolios')).toBeInTheDocument();
			expect(screen.getByText('home:ratings')).toBeInTheDocument();
		});

		it('should display formatted currency values', async () => {
			renderApp({ initialPath: '/home' });

			// Wait for data to load
			await waitFor(
				() => {
					// Insurance value: 15,000,000 formatted as German number
					expect(screen.getByText('15.000.000')).toBeInTheDocument();
				},
				{ timeout: 10000 }
			);

			// Time value: 12,000,000
			expect(screen.getByText('12.000.000')).toBeInTheDocument();

			// Short term renovation costs: 500,000
			expect(screen.getByText('500.000')).toBeInTheDocument();

			// Mid term renovation costs: 1,200,000
			expect(screen.getByText('1.200.000')).toBeInTheDocument();
		});
	});

	describe('Open activities card', () => {
		it('should show activity count in card title', async () => {
			renderApp({ initialPath: '/home' });

			// The card title includes the count from fixture (1 activity)
			await waitFor(
				() => {
					expect(screen.getByText('home:openActivities (1)')).toBeInTheDocument();
				},
				{ timeout: 10000 }
			);
		});

		it('should display overdue activities in collapsed section', async () => {
			const { user } = renderApp({ initialPath: '/home' });

			// Wait for activities card to load
			await waitFor(
				() => {
					expect(screen.getByText('home:openActivities (1)')).toBeInTheDocument();
				},
				{ timeout: 10000 }
			);

			// The activity with dueAt=now is overdue, so it's in a collapsed section
			// Find and click the collapse panel to expand it
			const overdueSection = await screen.findByText(/home:overdue/);
			expect(overdueSection).toBeInTheDocument();

			// Click to expand the overdue section
			await user.click(overdueSection);

			// Now the activity details should be visible
			await waitFor(() => {
				expect(
					screen.getByText('Review inspection report')
				).toBeInTheDocument();
			});

			// Activity content
			expect(
				screen.getByText('Please review the inspection report for Building A')
			).toBeInTheDocument();
		});
	});

	describe('Recent actions card', () => {
		it('should display recent actions card and data', async () => {
			renderApp({ initialPath: '/home' });

			// Wait for recent actions card to load
			await waitFor(
				() => {
					expect(screen.getByText('home:recentActions')).toBeInTheDocument();
				},
				{ timeout: 10000 }
			);

			// Wait for the data to load - Building A from the recent actions fixture
			await waitFor(
				() => {
					// The building name should appear as a link
					const buildingLink = screen.getByRole('link', { name: 'Building A' });
					expect(buildingLink).toBeInTheDocument();
				},
				{ timeout: 10000 }
			);
		});
	});

	describe('Statistics card', () => {
		it('should display building condition statistics', async () => {
			renderApp({ initialPath: '/home' });

			// Wait for statistics card
			await waitFor(
				() => {
					expect(screen.getByText('home:statistics')).toBeInTheDocument();
				},
				{ timeout: 10000 }
			);

			// Building condition label
			expect(screen.getByText('home:buildingCondition')).toBeInTheDocument();

			// ZN Portfolio value
			expect(screen.getByText('0.79')).toBeInTheDocument();
		});
	});

	describe('Map card', () => {
		it('should render the map card with title', async () => {
			renderApp({ initialPath: '/home' });

			// Wait for map card title
			await waitFor(
				() => {
					expect(screen.getByText('home:mapTitle')).toBeInTheDocument();
				},
				{ timeout: 10000 }
			);

			// The mocked Google Map should be present
			await waitFor(
				() => {
					expect(screen.getByTestId('google-map')).toBeInTheDocument();
				},
				{ timeout: 10000 }
			);
		});
	});

	describe('Session state verification', () => {
		it('should maintain session state throughout dashboard navigation', async () => {
			renderApp({ initialPath: '/home' });

			// Verify session is still open after dashboard renders
			await waitFor(
				() => {
					const state = useSessionStore.getState();
					expect(state.state).toBe(SessionState.open);
					expect(state.sessionInfo?.account?.name).toBe('Default Account');
					expect(state.sessionInfo?.tenant?.name).toBe('Test Tenant');
				},
				{ timeout: 10000 }
			);
		});
	});
});
