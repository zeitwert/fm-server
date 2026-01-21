import { theme } from "antd";
import type { CSSProperties } from "react";

/**
 * Style hook that provides design tokens and common style patterns.
 *
 * Combines Ant Design's useToken() with commonly used style patterns
 * to create a consistent, theme-aware styling approach.
 *
 * @example
 * const { token, styles } = useStyles();
 *
 * // Use token values directly
 * <div style={{ color: token.colorPrimary }}>...</div>
 *
 * // Use pre-built style patterns
 * <Typography.Text style={styles.readonlyField}>...</Typography.Text>
 */
export function useStyles() {
	const { token } = theme.useToken();

	// Pre-built style patterns using theme tokens
	const styles = {
		/**
		 * Style for read-only form fields with underline.
		 * Use with Typography.Text for displaying non-editable values.
		 */
		readonlyField: {
			display: "block",
			width: "100%",
			fontWeight: 600,
			borderBottom: `1px solid ${token.colorBorder}`,
		} as CSSProperties,

		/**
		 * Style for read-only field with text alignment.
		 */
		readonlyFieldAligned: (align?: "left" | "center" | "right"): CSSProperties => ({
			display: "block",
			width: "100%",
			fontWeight: 600,
			borderBottom: `1px solid ${token.colorBorder}`,
			textAlign: align,
		}),

		/**
		 * Icon styling with primary color.
		 */
		primaryIcon: {
			fontSize: 24,
			color: token.colorPrimary,
			display: "inline-flex",
			alignItems: "center",
		} as CSSProperties,

		/**
		 * Required asterisk indicator.
		 */
		requiredAsterisk: {
			color: token.colorError,
			marginLeft: 2,
		} as CSSProperties,

		/**
		 * Help icon styling.
		 */
		helpIcon: {
			marginLeft: 4,
			color: token.colorTextSecondary,
		} as CSSProperties,

		/**
		 * Form item margin.
		 */
		formItemMargin: {
			marginBottom: token.margin,
		} as CSSProperties,

		/**
		 * Card header with layout background.
		 */
		cardHeader: {
			marginBottom: token.margin,
			background: token.colorBgLayout,
			flexShrink: 0,
		} as CSSProperties,

		/**
		 * Full-height loading container.
		 */
		loadingContainer: {
			minHeight: "100vh",
			display: "flex",
			justifyContent: "center",
			alignItems: "center",
			background: token.colorBgContainer,
		} as CSSProperties,

		/**
		 * Inline loading with padding.
		 */
		loadingInline: {
			display: "flex",
			justifyContent: "center",
			padding: 48,
		} as CSSProperties,

		/**
		 * Pagination footer styling.
		 */
		paginationFooter: {
			padding: token.padding,
			borderTop: `1px solid ${token.colorBorderSecondary}`,
			display: "flex",
			justifyContent: "flex-end",
			flexShrink: 0,
			background: token.colorBgContainer,
		} as CSSProperties,
	};

	return { token, styles };
}
