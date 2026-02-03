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
 * Form area theme configuration.
 *
 * Applied via nested ConfigProvider in ItemPageLayout to style
 * form containers and controls differently from the rest of the app.
 * Adjust these tokens to control form background, borders, spacing, etc.
 */
export const formTheme: ThemeConfig = {
	token: {
		// No border radius for form fields
		borderRadius: 0,
		borderRadiusLG: 0,
		borderRadiusSM: 0,
	},
	components: {
		Card: {
			// Form card background color
			colorBgContainer: "rgb(242, 242, 242)",
		},
		Input: {
			// White background for form inputs
			colorBgContainer: "#ffffff",
			activeBg: "#ffffff",
			hoverBg: "#ffffff",
			// Border color when editing
			colorBorder: "rgb(118, 118, 118)",
			activeBorderColor: "rgb(118, 118, 118)",
			hoverBorderColor: "rgb(118, 118, 118)",
		},
		Select: {
			// White background for select controls
			colorBgContainer: "#ffffff",
			// Border color when editing
			colorBorder: "rgb(118, 118, 118)",
			activeBorderColor: "rgb(118, 118, 118)",
			hoverBorderColor: "rgb(118, 118, 118)",
		},
		InputNumber: {
			// White background for number inputs
			colorBgContainer: "#ffffff",
			// Border color when editing
			colorBorder: "rgb(118, 118, 118)",
			activeBorderColor: "rgb(118, 118, 118)",
			hoverBorderColor: "rgb(118, 118, 118)",
		},
		DatePicker: {
			// White background for date pickers
			colorBgContainer: "#ffffff",
			// Border color when editing
			colorBorder: "rgb(118, 118, 118)",
			activeBorderColor: "rgb(118, 118, 118)",
			hoverBorderColor: "rgb(118, 118, 118)",
		},
		Tabs: {
			// Tab underline color
			inkBarColor: "#d9d9d9",
		},
		Form: {
			// Label text color
			labelColor: "rgb(51, 51, 51)",
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
 * These match the loaded Gotham Narrow SSM font weights.
 */
export const fontWeight = {
	normal: 400,
	medium: 500,
	bold: 700,
} as const;
