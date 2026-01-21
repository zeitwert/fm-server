import { Typography } from "antd";
import { useStyles } from "../../hooks/useStyles";
import { getFieldContainerStyle } from "../../styles";
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
	const { styles, token } = useStyles();

	// Use a simplified version of AfField layout without form context dependency
	return (
		<div style={getFieldContainerStyle(size)}>
			<div style={styles.formItemMargin}>
				{label && (
					<label
						style={{
							display: "block",
							marginBottom: token.marginSM,
							color: token.colorTextSecondary,
							fontSize: 14,
						}}
					>
						{label}
					</label>
				)}
				{multiline ? (
					<Typography.Paragraph className="af-readonly-field-text">
						{value ?? "\u00A0"}
					</Typography.Paragraph>
				) : (
					<Typography.Text>{value ?? "\u00A0"}</Typography.Text>
				)}
			</div>
		</div>
	);
}
