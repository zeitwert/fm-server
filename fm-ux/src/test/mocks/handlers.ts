import { http, HttpResponse } from 'msw';
import {
	singleTenantUser,
	singleAccountTenantInfo,
	activeSessionInfo,
	homeOverview,
	homeOpenActivities,
	homeRecentActions,
	homeBuildings,
} from './fixtures';

// Default handlers - single tenant, single account (auto-complete flow)
export const handlers = [
	// Authentication
	http.post('/rest/session/authenticate', async ({ request }) => {
		const body = (await request.json()) as { email: string; password: string };

		// Simulate invalid credentials
		if (body.password === 'invalid') {
			return HttpResponse.json(
				{ errors: [{ detail: 'Invalid credentials' }] },
				{ status: 401 }
			);
		}

		return HttpResponse.json(singleTenantUser);
	}),

	// Tenant info
	http.get('/rest/app/tenantInfo/:tenantId', () => {
		return HttpResponse.json(singleAccountTenantInfo);
	}),

	// Session activation
	http.post('/rest/session/activate', () => {
		return HttpResponse.json(activeSessionInfo);
	}),

	// Logout
	http.post('/rest/session/logout', () => {
		return HttpResponse.json({ success: true });
	}),

	// Home dashboard APIs
	http.get('/rest/home/overview/:accountId', () => {
		return HttpResponse.json(homeOverview);
	}),

	http.get('/rest/home/openActivities/:accountId', () => {
		return HttpResponse.json(homeOpenActivities);
	}),

	http.get('/rest/home/recentActions/:accountId', () => {
		return HttpResponse.json(homeRecentActions);
	}),

	http.get('/api/building/buildings', () => {
		return HttpResponse.json(homeBuildings);
	}),
];
