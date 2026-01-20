import { Card, Typography } from "antd";
import type { ReactNode } from "react";

interface AfFieldGroupProps {
	/** Group legend/title */
	legend?: string;
	/** Field components */
	children: ReactNode;
}

/**
 * Groups fields with an optional legend.
 *
 * Replaces `FieldGroup` from fm-ui. Uses Ant Design Card for consistent styling.
 *
 * @example
 * <AfFieldGroup legend="Grunddaten">
 *   <AfFieldRow>
 *     <AfInput name="name" label="Name" />
 *   </AfFieldRow>
 * </AfFieldGroup>
 */
export function AfFieldGroup({ legend, children }: AfFieldGroupProps) {
	return (
		<Card
			size="small"
			title={legend && <Typography.Text strong>{legend}</Typography.Text>}
			style={{ marginBottom: 16 }}
			styles={{ body: { paddingTop: 16, paddingBottom: 0 } }}
		>
			{children}
		</Card>
	);
}
