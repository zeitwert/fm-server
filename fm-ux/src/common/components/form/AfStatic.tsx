import { Typography } from "antd";
import type { AfFieldProps } from "../../types";

interface AfStaticProps extends Omit<AfFieldProps, "name" | "disabled" | "readOnly"> {
	/** The value to display */
	value?: string | number | null;
	/** Whether to preserve whitespace and line breaks */
	multiline?: boolean;
}

/**
 * Read-only display field for static values.
 *
 * Replaces `Static` from fm-ui. Use this when you need to display a value
 * that is not editable and not connected to form state.
 *
 * @example
 * <AfStatic label="Erstellt am" value={formatDate(createdAt)} />
 * <AfStatic label="Beschreibung" value={description} multiline />
 */
export function AfStatic({ value, multiline, label, size = 12 }: AfStaticProps) {
	// Use a simplified version of AfField layout without form context dependency
	return (
		<div style={{ width: `${(size / 12) * 100}%`, padding: "0 8px", boxSizing: "border-box" }}>
			<div style={{ marginBottom: 16 }}>
				{label && (
					<label
						style={{
							display: "block",
							marginBottom: 8,
							color: "rgba(0, 0, 0, 0.45)",
							fontSize: 14,
						}}
					>
						{label}
					</label>
				)}
				{multiline ? (
					<Typography.Paragraph style={{ whiteSpace: "pre-wrap", margin: 0 }}>
						{value ?? "\u00A0"}
					</Typography.Paragraph>
				) : (
					<Typography.Text>{value ?? "\u00A0"}</Typography.Text>
				)}
			</div>
		</div>
	);
}
