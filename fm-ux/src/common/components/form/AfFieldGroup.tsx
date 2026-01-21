import type { ReactNode } from "react";

interface AfFieldGroupProps {
	/** Bold group legend/title */
	legend?: string;
	/** Field components */
	children: ReactNode;
	/** Optional CSS class */
	className?: string;
}

/**
 * Groups fields with an optional legend header.
 *
 * Replaces `FieldGroup` from fm-ui. Uses native HTML fieldset for minimal styling
 * (no borders, no padding) - acts as a semantic grouping with just a header.
 *
 * @example
 * <AfFieldGroup legend="Grunddaten">
 *   <AfFieldRow>
 *     <AfInput name="name" label="Name" />
 *   </AfFieldRow>
 * </AfFieldGroup>
 */
export function AfFieldGroup({ legend, children, className }: AfFieldGroupProps) {
	return (
		<fieldset
			style={{ border: "none", padding: 0, margin: 0, marginTop: 16 }}
			className={className}
		>
			{legend && (
				<legend style={{ fontWeight: 600, whiteSpace: "nowrap", marginBottom: 16 }}>
					{legend}
				</legend>
			)}
			{children}
		</fieldset>
	);
}
