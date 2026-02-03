import type { ThemeConfig } from "antd";

/**
 * Application design tokens.
 *
 * These are semantic aliases for Ant Design's token system.
 * Use these constants with useToken() hook for consistent theming.
 */
export const appTheme: ThemeConfig = {
	token: {
		// Typography
		fontFamily:
			"'Gotham Narrow SSM', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",

		// Colors are inherited from Ant Design defaults
		// colorPrimary: '#1677ff'  - Ant Design default
		// colorBorder: '#d9d9d9'   - Ant Design default
		// colorTextSecondary: '#00000073' - Ant Design default (rgba(0,0,0,0.45))
		// colorBgContainer: '#ffffff' - Ant Design default
		// colorBgLayout: '#f5f5f5'  - Ant Design default

		// Spacing
		margin: 16,
		marginSM: 8,
		marginXS: 4,
		padding: 16,
		paddingSM: 8,
		paddingXS: 4,
	},
	components: {
		Input: {
			// Remove light blue background on focus/hover
			activeBg: "#ffffff",
			hoverBg: "#ffffff",
		},
	},
};

/**
 * Spacing scale constants (in pixels).
 * Use with useToken() for reactive theme support.
 */
export const spacing = {
	xs: 4,
	sm: 8,
	md: 16,
	lg: 24,
	xl: 32,
} as const;

/**
 * Font weight constants.
 */
export const fontWeight = {
	normal: 400,
	medium: 500,
	semibold: 600,
	bold: 700,
} as const;
