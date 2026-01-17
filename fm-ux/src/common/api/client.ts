import axios, { AxiosError, AxiosRequestConfig, AxiosResponse } from 'axios';

export const JSON_CONTENT_TYPE = 'application/json';
export const API_CONTENT_TYPE = 'application/vnd.api+json';

const SESSION_INFO_KEY = 'fm-ux-session-info';
const SESSION_STATE_KEY = 'fm-ux-session-state';
const TENANT_INFO_KEY = 'fm-ux-tenant-info';

// Create axios instance with default config
const apiClient = axios.create({
	withCredentials: true, // Allow server to set cookies (session authentication)
	headers: {
		Accept: `${JSON_CONTENT_TYPE},${API_CONTENT_TYPE}`,
	},
});

// Response interceptor for 401 handling
apiClient.interceptors.response.use(
	(response: AxiosResponse): AxiosResponse => response,
	(error: AxiosError) => {
		if (error.response?.status === 401) {
			sessionStorage.removeItem(SESSION_STATE_KEY);
			sessionStorage.removeItem(SESSION_INFO_KEY);
			sessionStorage.removeItem(TENANT_INFO_KEY);
			// Redirect to login with current path as redirect parameter
			const currentPath = window.location.pathname + window.location.search;
			const redirectParam = currentPath && currentPath !== '/' && currentPath !== '/login' 
				? `?redirect=${encodeURIComponent(currentPath)}` 
				: '';
			window.location.replace(`/login${redirectParam}`);
		}

		if (error.response) {
			const data = error.response.data as { errors?: Array<{ detail?: string }> };
			if (data?.errors && Array.isArray(data.errors) && data.errors.length > 0) {
				return Promise.reject(data.errors[0]);
			}
			return Promise.reject({
				detail: error.message,
				status: error.response.status,
				title: 'Unknown error',
			});
		}

		return Promise.reject({
			detail: error.message,
			status: '',
			title: 'Unknown error',
		});
	}
);

// URL helper functions
export const getRestUrl = (module: string, path: string): string => {
	return `/rest/${module}/${path}`;
};

export const getApiUrl = (module: string, path: string): string => {
	return `/api/${module}/${path}`;
};

export const getEnumUrl = (module: string, enumName: string): string => {
	return `/enum/${module}/${enumName}`;
};

export const getLogoUrl = (type: 'account' | 'tenant', id: string): string => {
	if (type === 'account') {
		return `/rest/account/accounts/${id}/logo`;
	}
	return `/rest/oe/tenants/${id}/logo`;
};

// API methods
export const api = {
	get<T = unknown>(url: string, config?: AxiosRequestConfig) {
		return apiClient.get<T>(url, config);
	},

	post<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig) {
		return apiClient.post<T>(url, data, config);
	},

	put<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig) {
		return apiClient.put<T>(url, data, config);
	},

	patch<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig) {
		return apiClient.patch<T>(url, data, config);
	},

	delete<T = unknown>(url: string, config?: AxiosRequestConfig) {
		return apiClient.delete<T>(url, config);
	},

	// Special login method that creates a fresh axios instance to avoid interceptors
	login<T = unknown>(url: string, data?: unknown) {
		return axios.create({ withCredentials: true }).post<T>(url, data, {
			headers: {
				Accept: `${JSON_CONTENT_TYPE},${API_CONTENT_TYPE}`,
			},
		});
	},
};

// Storage keys export for use in session store
export { SESSION_INFO_KEY, SESSION_STATE_KEY, TENANT_INFO_KEY };
