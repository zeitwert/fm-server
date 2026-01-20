/**
 * ItemPageHeader component for entity detail pages.
 *
 * Displays the entity icon, type label, title, optional detail fields,
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
	/** Label for the entity type (e.g., "Immobilie", "Kontakt") */
	entityLabel: string;
	/** Title of the specific entity (e.g., building name, contact name) */
	title: string;
	/** Optional subtitle for additional context */
	subtitle?: string;
	/** Array of detail fields to display below the title */
	details?: HeaderDetail[];
	/** Action buttons (e.g., EditControls, entity-specific actions) */
	actions?: ReactNode;
}

export function ItemPageHeader({
	icon,
	entityLabel,
	title,
	subtitle,
	details,
	actions,
}: ItemPageHeaderProps) {
	return (
		<Card style={{ marginBottom: 16 }}>
			<Row justify="space-between" align="top" wrap={false}>
				<Col flex="auto">
					<Space align="start" size="middle">
						{icon && <span style={{ fontSize: 24, color: "#1677ff" }}>{icon}</span>}
						<div>
							<Text type="secondary" style={{ fontSize: 12 }}>
								{entityLabel}
							</Text>
							<Title level={4} style={{ margin: 0 }}>
								{title}
							</Title>
							{subtitle && (
								<Text type="secondary" style={{ fontSize: 14 }}>
									{subtitle}
								</Text>
							)}
						</div>
					</Space>
				</Col>
				{actions && <Col flex="none">{actions}</Col>}
			</Row>

			{details && details.length > 0 && (
				<Row gutter={[24, 8]} style={{ marginTop: 16 }}>
					{details.map((detail, index) => (
						<Col key={index}>
							<Text type="secondary" style={{ fontSize: 12 }}>
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
						</Col>
					))}
				</Row>
			)}
		</Card>
	);
}
