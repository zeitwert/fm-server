import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import { tanstackRouter } from "@tanstack/router-plugin/vite";
import path from "path";
export default defineConfig({
    plugins: [tanstackRouter(), react()],
    resolve: {
        alias: {
            "@": path.resolve(__dirname, "./src"),
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
