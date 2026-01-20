import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import { tanstackRouter } from "@tanstack/router-plugin/vite";
export default defineConfig({
    plugins: [tanstackRouter(), react()],
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
