import type { ThemeConfig } from "antd";

export const appTheme: ThemeConfig = {
	components: {
		Input: {
			// Remove light blue background on focus/hover
			activeBg: "#ffffff",
			hoverBg: "#ffffff",
		},
	},
};
