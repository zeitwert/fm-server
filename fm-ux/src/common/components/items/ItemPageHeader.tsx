/**
 * ItemPageHeader component for entity detail pages.
 *
 * Displays the entity icon, title, optional detail fields,
 * and action buttons in a consistent layout.
 */

import { Card, Row, Col, Space, Typography } from "antd";
import { Link } from "@tanstack/react-router";
import type { ReactNode } from "react";

const { Title, Text } = Typography;

export interface HeaderDetail {
	/** Label for the detail field */
	label: string;
	/** Content to display (can be text, link, or custom node) */
	content?: ReactNode;
	/** Optional link URL (if provided, content is wrapped in a Link) */
	link?: string;
	/** Optional icon to display before the content */
	icon?: ReactNode;
}

export interface ItemPageHeaderProps {
	/** Icon representing the entity type */
	icon?: ReactNode;
	/** Title of the specific entity (e.g., building name, contact name) */
	title: string;
	/** Array of detail fields to display */
	details?: HeaderDetail[];
	/** Action buttons (e.g., EditControls, entity-specific actions) */
	actions?: ReactNode;
}

export function ItemPageHeader({ icon, title, details, actions }: ItemPageHeaderProps) {
	return (
		<Card
			style={{
				marginBottom: 16,
				background: "#f5f5f5",
			}}
			styles={{
				body: {
					padding: "8px 16px",
				},
			}}
		>
			<Row justify="space-between" align="middle" wrap={false}>
				<Col flex="none">
					<Space size="middle">
						{icon && <span style={{ fontSize: 24, color: "#1677ff" }}>{icon}</span>}
						<Title level={4} style={{ margin: 0 }}>
							{title}
						</Title>
					</Space>
				</Col>
				{details && details.length > 0 && (
					<Col flex="auto" style={{ marginLeft: 48 }}>
						<Space size={40}>
							{details.map((detail, index) => (
								<div key={index}>
									<Text type="secondary" style={{ fontSize: 12, display: "block" }}>
										{detail.label}
									</Text>
									<div style={{ display: "flex", alignItems: "center", gap: 4 }}>
										{detail.icon}
										{detail.link ? (
											<Link to={detail.link}>{detail.content}</Link>
										) : (
											<span>{detail.content ?? "-"}</span>
										)}
									</div>
								</div>
							))}
						</Space>
					</Col>
				)}
				{actions && <Col flex="none">{actions}</Col>}
			</Row>
		</Card>
	);
}
