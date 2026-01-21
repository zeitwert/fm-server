import type { ReactNode } from "react";

interface AfFieldRowProps {
	/** Field components */
	children: ReactNode;
	/** Gap between fields in pixels (default: 0, fields have internal padding) */
	gutter?: number;
}

/**
 * Horizontal layout container for form fields.
 *
 * Replaces `FieldRow` from fm-ui. Uses flexbox for layout.
 * Fields inside determine their own width via the `size` prop (1-12 grid system).
 *
 * @example
 * <AfFieldRow>
 *   <AfInput name="buildingNr" label="Nr" size={3} />
 *   <AfInput name="name" label="Name" size={9} />
 * </AfFieldRow>
 */
export function AfFieldRow({ children, gutter = 0 }: AfFieldRowProps) {
	return (
		<div className="af-field-row" style={gutter > 0 ? { gap: `${gutter}px` } : undefined}>
			{children}
		</div>
	);
}
