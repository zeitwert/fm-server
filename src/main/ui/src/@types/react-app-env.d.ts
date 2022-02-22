/// <reference types="react-scripts" />

declare namespace NodeJS {
	interface ProcessEnv {
		NODE_ENV: "development" | "production" | "test";
		PUBLIC_URL: string;

		// Version (provided by build script)
		REACT_APP_VERSION: string;
		REACT_APP_IMAGE: string;

		// Logging
		REACT_APP_LOG_LEVEL: string;

		// Server URLs
		REACT_APP_API_BASE_URL: string;
	}
}

interface Window {}
