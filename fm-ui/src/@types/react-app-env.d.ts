/// <reference types="react-scripts" />

declare namespace NodeJS {
	interface ProcessEnv {

		NODE_ENV: "development" | "production" | "test";

		// Logging
		REACT_APP_LOG_LEVEL: string;

		// Server URLs
		REACT_APP_API_BASE_URL: string;
		REACT_APP_SERVER_BASE_URL: string;
	}
}

interface Window { }
