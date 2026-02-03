/**
 * ItemPageHeader component for entity detail pages.
 *
 * Displays the entity icon, title, optional detail fields,
 * and action buttons in a consistent layout.
 */

import { Card, Row, Col, Typography } from "antd";
import { Link } from "@tanstack/react-router";
import { useStyles } from "../../hooks/useStyles";
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
	const { styles, token } = useStyles();

	return (
		<Card
			style={styles.cardHeader}
			styles={{
				body: {
					padding: `${token.padding}px ${token.padding}px`,
					borderRadius: 0,
					backgroundColor: "rgb(225, 225, 225)",
				},
			}}
		>
			<Row justify="space-between" align="middle" wrap={false}>
				<Col flex="none">
					<div style={{ display: "flex", alignItems: "center", gap: 12 }}>
						{icon && <span style={styles.primaryIcon}>{icon}</span>}
						<Title level={4} style={{ margin: 0, lineHeight: "24px" }}>
							{title}
						</Title>
					</div>
				</Col>
				{details && details.length > 0 && (
					<Col flex="auto" className="af-ml-48">
						<div style={{ display: "flex", alignItems: "center", gap: 40 }}>
							{details.map((detail, index) => (
								<div key={index}>
									<Text type="secondary" style={{ fontSize: 12, display: "block" }}>
										{detail.label}
									</Text>
									<div className="af-flex af-gap-4" style={{ alignItems: "center" }}>
										{detail.icon}
										{detail.link ? (
											<Link to={detail.link}>{detail.content}</Link>
										) : (
											<span>{detail.content ?? "-"}</span>
										)}
									</div>
								</div>
							))}
						</div>
					</Col>
				)}
				{actions && <Col flex="none">{actions}</Col>}
			</Row>
		</Card>
	);
}
