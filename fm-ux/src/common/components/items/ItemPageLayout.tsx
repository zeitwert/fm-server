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
}

export function ItemPageLayout({ children, rightPanel }: ItemPageLayoutProps) {
	const { rightPanelCollapsed, toggleRightPanel } = useShellStore();

	// No right panel provided - render full width
	if (!rightPanel) {
		return <div>{children}</div>;
	}

	// Panel collapsed - full width with toggle button
	if (rightPanelCollapsed) {
		return (
			<div style={{ position: "relative" }}>
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
		<Row gutter={16}>
			<Col xs={24} lg={16} xl={18}>
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
