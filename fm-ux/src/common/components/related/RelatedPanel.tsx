/**
 * RelatedPanel component for the collapsible right panel in entity detail pages.
 *
 * Uses Ant Design Collapse to display related content sections like
 * notes, tasks, documents, and activity timeline.
 */

import { useState } from "react";
import { Card, Collapse, Space, Badge } from "antd";
import type { ReactNode } from "react";

export interface RelatedSection {
	/** Unique key for the section */
	key: string;
	/** Section label */
	label: string;
	/** Optional count badge (e.g., number of notes) */
	count?: number;
	/** Optional status badge for warnings/errors */
	badge?: "warning" | "error";
	/** Section content */
	children: ReactNode;
}

export interface RelatedPanelProps {
	/** Array of sections to display */
	sections: RelatedSection[];
	/** Keys of sections that should be expanded by default */
	defaultActiveKeys?: string[];
}

export function RelatedPanel({ sections, defaultActiveKeys }: RelatedPanelProps) {
	const [activeKeys, setActiveKeys] = useState<string[]>(
		defaultActiveKeys ?? (sections[0] ? [sections[0].key] : [])
	);

	return (
		<Card size="small" styles={{ body: { padding: 0 } }}>
			<Collapse
				activeKey={activeKeys}
				onChange={(keys) => setActiveKeys(keys as string[])}
				ghost
				expandIconPosition="end"
				items={sections.map((section) => ({
					key: section.key,
					label: (
						<Space size="small">
							<span>{section.label}</span>
							{section.count !== undefined && section.count > 0 && (
								<Badge
									count={section.count}
									size="small"
									style={{
										backgroundColor: "#1677ff",
									}}
								/>
							)}
							{section.badge === "warning" && <Badge status="warning" />}
							{section.badge === "error" && <Badge status="error" />}
						</Space>
					),
					children: section.children,
				}))}
			/>
		</Card>
	);
}
