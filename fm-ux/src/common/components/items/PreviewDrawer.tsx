/**
 * PreviewDrawer component for side panel previews in list views.
 *
 * Provides a standardized wrapper for entity previews that slide in from the right.
 */

import { Drawer, Spin } from "antd";
import type { ReactNode } from "react";

export interface PreviewDrawerProps {
	/** Whether the drawer is visible */
	open: boolean;
	/** Drawer title */
	title?: ReactNode;
	/** Width of the drawer (default: 400) */
	width?: number;
	/** Callback when the drawer is closed */
	onClose: () => void;
	/** Loading state */
	loading?: boolean;
	/** Content to display in the drawer */
	children: ReactNode;
}

export function PreviewDrawer({
	open,
	title,
	width = 400,
	onClose,
	loading = false,
	children,
}: PreviewDrawerProps) {
	return (
		<Drawer
			open={open}
			title={title}
			width={width}
			onClose={onClose}
			destroyOnHidden
			styles={{
				body: { paddingTop: 0 },
			}}
		>
			{loading ? (
				<div
					style={{
						display: "flex",
						justifyContent: "center",
						alignItems: "center",
						height: 200,
					}}
				>
					<Spin />
				</div>
			) : (
				children
			)}
		</Drawer>
	);
}
