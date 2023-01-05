import Axios, { AxiosError, AxiosHeaders, AxiosPromise, AxiosRequestConfig, AxiosResponse } from "axios";
import Logger from "loglevel";
import { AUTH_HEADER_ITEM, SESSION_INFO_ITEM, SESSION_STATE_ITEM } from "../config/Constants";

export const JSON_CONTENT_TYPE = "application/json";
export const API_CONTENT_TYPE = "application/vnd.api+json";

export const API_HEADERS = {
	"Content-Type": API_CONTENT_TYPE
};

function getHeaders(configHeaders: any): AxiosHeaders {
	const header = new AxiosHeaders(configHeaders ?? {});
	header.setAccept(JSON_CONTENT_TYPE + "," + API_CONTENT_TYPE);
	if (sessionStorage.getItem(AUTH_HEADER_ITEM)) {
		header.set("Authorization", sessionStorage.getItem(AUTH_HEADER_ITEM));
	}
	return header;
}

function getConfig(config?: AxiosRequestConfig): AxiosRequestConfig {
	const headers = getHeaders(config?.headers);
	delete config?.headers;
	return {
		withCredentials: true, // allow server to set cookies
		...config,
		headers: headers
	};
}

Axios.interceptors.request.use(
	function (config: AxiosRequestConfig): AxiosRequestConfig {
		// Do something before request is sent
		return config;
	},
	function (error: any) {
		// Do something with request error
		Logger.error("Request crashed: ", error);
		return Promise.reject(error);
	}
);

Axios.interceptors.response.use(
	(response: AxiosResponse): AxiosResponse => {
		return response;
	},
	(error: AxiosError<any, any>) => {
		if (error.response) {
			if (401 === error.response.status) {
				sessionStorage.removeItem(SESSION_STATE_ITEM);
				sessionStorage.removeItem(SESSION_INFO_ITEM);
				sessionStorage.removeItem(AUTH_HEADER_ITEM);
				window.location.replace("/");
			} else if (
				error.response.data &&
				Array.isArray(error.response.data.errors) &&
				error.response.data.errors.length > 0
			) {
				// For simplicity, we only return the first error message.
				return Promise.reject(error.response.data.errors[0]);
			}
			return Promise.reject({
				detail: error.message,
				status: error.response.status,
				title: "Unknown error"
			});
		}
		return Promise.reject({
			detail: error.message,
			status: "",
			title: "Unknown error"
		});
	}
);

export const API = {
	get<T = any>(url: string, config?: AxiosRequestConfig): AxiosPromise<T> {
		return Axios.get(url, getConfig(config));
	},
	login<T = any>(url: string, data?: any): AxiosPromise<T> {
		return Axios.create().post(url, data, getConfig());
	},
	post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): AxiosPromise<T> {
		return Axios.post(url, data, getConfig(config));
	},
	put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): AxiosPromise<T> {
		return Axios.put(url, data, getConfig(config));
	},
	patch<T = any>(url: string, data?: any, config?: AxiosRequestConfig): AxiosPromise<T> {
		return Axios.patch(url, data, getConfig(config));
	},
	delete(url: string, config?: AxiosRequestConfig): AxiosPromise {
		return Axios.delete(url, getConfig(config));
	}
};
