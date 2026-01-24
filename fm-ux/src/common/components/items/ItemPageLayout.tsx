/**
 * ItemPageLayout component providing a split layout with an optional collapsible right panel.
 *
 * Uses the shell store to persist the collapsed state across page navigations.
 */

import { Row, Col, Button } from "antd";
import { MenuFoldOutlined, MenuUnfoldOutlined } from "@ant-design/icons";
import { useShellStore } from "../../../shell/shellStore";
import type { ReactNode } from "react";

export interface ItemPageLayoutProps {
	/** Main content (typically form tabs) */
	children: ReactNode;
	/** Content for the right panel (typically RelatedPanel with notes, tasks, etc.) */
	rightPanel?: ReactNode;
	/** When true, force full-width mode hiding the right panel entirely (no toggle button) */
	fullWidth?: boolean;
}

export function ItemPageLayout({ children, rightPanel, fullWidth }: ItemPageLayoutProps) {
	const { rightPanelCollapsed, toggleRightPanel } = useShellStore();

	// Full-width mode forced by tab configuration - render without any panel toggle
	if (fullWidth) {
		return <div style={{ flex: 1, minHeight: 0 }}>{children}</div>;
	}

	// No right panel provided - render full width
	if (!rightPanel) {
		return <div style={{ flex: 1, minHeight: 0 }}>{children}</div>;
	}

	// Panel collapsed - full width with toggle button
	if (rightPanelCollapsed) {
		return (
			<div style={{ position: "relative", flex: 1, minHeight: 0 }}>
				<Button
					icon={<MenuUnfoldOutlined />}
					onClick={toggleRightPanel}
					title="Panel einblenden"
					aria-label="common:showPanel"
					className="af-panel-toggle"
				/>
				{children}
			</div>
		);
	}

	// Panel expanded - split layout
	return (
		<Row gutter={16} style={{ flex: 1, minHeight: 0 }}>
			<Col xs={24} lg={16} xl={18} style={{ display: "flex", flexDirection: "column" }}>
				{children}
			</Col>
			<Col xs={24} lg={8} xl={6}>
				<div style={{ position: "relative" }}>
					<Button
						icon={<MenuFoldOutlined />}
						onClick={toggleRightPanel}
						size="small"
						title="Panel ausblenden"
						aria-label="common:hidePanel"
						className="af-panel-toggle-inner"
					/>
					{rightPanel}
				</div>
			</Col>
		</Row>
	);
}
