import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import { tanstackRouter } from "@tanstack/router-plugin/vite";
import { fileURLToPath, URL } from "node:url";

export default defineConfig({
	plugins: [tanstackRouter(), react()],
	resolve: {
		alias: {
			"@": fileURLToPath(new URL("./src", import.meta.url)),
		},
	},
	server: {
		proxy: {
			"/rest": {
				target: "http://localhost:8080",
				changeOrigin: true,
			},
			"/api": {
				target: "http://localhost:8080",
				changeOrigin: true,
			},
			"/enum": {
				target: "http://localhost:8080",
				changeOrigin: true,
			},
		},
	},
});
