import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { RouterProvider, createMemoryHistory, createRouter } from '@tanstack/react-router';
import { render, type RenderOptions } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { ReactElement, ReactNode } from 'react';
import { routeTree } from '../routeTree.gen';

// Create a fresh QueryClient for each test
function createTestQueryClient() {
	return new QueryClient({
		defaultOptions: {
			queries: {
				retry: false,
				gcTime: 0,
			},
		},
	});
}

// Create a test router with memory history
function createTestRouter(initialPath: string = '/login') {
	const memoryHistory = createMemoryHistory({
		initialEntries: [initialPath],
	});

	return createRouter({
		routeTree,
		history: memoryHistory,
	});
}

interface WrapperProps {
	children: ReactNode;
}

interface CustomRenderOptions extends Omit<RenderOptions, 'wrapper'> {
	initialPath?: string;
	queryClient?: QueryClient;
}

// Custom render function that wraps components with providers
function customRender(ui: ReactElement, options: CustomRenderOptions = {}) {
	const { initialPath = '/login', queryClient = createTestQueryClient(), ...renderOptions } = options;

	const testRouter = createTestRouter(initialPath);

	function Wrapper({ children }: WrapperProps) {
		return (
			<QueryClientProvider client={queryClient}>
				{children}
			</QueryClientProvider>
		);
	}

	return {
		user: userEvent.setup(),
		queryClient,
		router: testRouter,
		...render(ui, { wrapper: Wrapper, ...renderOptions }),
	};
}

// Render the full app with router
function renderApp(options: Omit<CustomRenderOptions, 'initialPath'> & { initialPath?: string } = {}) {
	const { initialPath = '/login', queryClient = createTestQueryClient(), ...renderOptions } = options;

	const testRouter = createTestRouter(initialPath);

	return {
		user: userEvent.setup(),
		queryClient,
		router: testRouter,
		...render(
			<QueryClientProvider client={queryClient}>
				<RouterProvider router={testRouter} />
			</QueryClientProvider>,
			renderOptions
		),
	};
}

// Re-export testing utilities
export * from '@testing-library/react';
export { customRender as render, renderApp, createTestQueryClient, createTestRouter, userEvent };
