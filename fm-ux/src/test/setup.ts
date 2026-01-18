import '@testing-library/jest-dom/vitest';
import type { ReactNode } from 'react';
import { afterAll, afterEach, beforeAll, vi } from 'vitest';
import { cleanup } from '@testing-library/react';
import { server } from './mocks/server';

// Mock react-i18next to return namespaced keys for deterministic testing
vi.mock('react-i18next', () => ({
	useTranslation: (ns: string = 'common') => ({
		t: (key: string) => `${ns}:${key}`,
		i18n: { language: 'en', changeLanguage: vi.fn() },
	}),
	Trans: ({ children }: { children: ReactNode }) => children,
	initReactI18next: { type: '3rdParty', init: vi.fn() },
}));

// Mock the i18n module to prevent initialization errors
vi.mock('../i18n', () => ({
	default: {
		language: 'en',
		changeLanguage: vi.fn(),
	},
	changeLanguage: vi.fn(),
}));

// Start MSW server before all tests
beforeAll(() => {
	server.listen({ onUnhandledRequest: 'error' });
});

// Reset handlers and cleanup after each test
afterEach(() => {
	server.resetHandlers();
	cleanup();
	// Clear session storage
	sessionStorage.clear();
});

// Close MSW server after all tests
afterAll(() => {
	server.close();
});

// Mock window.location.replace to prevent navigation errors in tests
Object.defineProperty(window, 'location', {
	value: {
		...window.location,
		replace: vi.fn(),
		pathname: '/login',
		search: '',
	},
	writable: true,
});

// Mock matchMedia for Ant Design components
Object.defineProperty(window, 'matchMedia', {
	writable: true,
	value: vi.fn().mockImplementation((query: string) => ({
		matches: false,
		media: query,
		onchange: null,
		addListener: vi.fn(),
		removeListener: vi.fn(),
		addEventListener: vi.fn(),
		removeEventListener: vi.fn(),
		dispatchEvent: vi.fn(),
	})),
});

// Mock ResizeObserver for components that use it
class ResizeObserverMock {
	observe = vi.fn();
	unobserve = vi.fn();
	disconnect = vi.fn();
}
(globalThis as unknown as { ResizeObserver: typeof ResizeObserverMock }).ResizeObserver =
	ResizeObserverMock;

// Mock scrollTo
window.scrollTo = vi.fn();
