/**
 * ActivityTimeline component for displaying entity activity history.
 *
 * Uses Ant Design Timeline to show a chronological list of actions.
 */

import { Timeline, Typography, Skeleton, Empty } from "antd";
import { useTranslation } from "react-i18next";

const { Text, Paragraph } = Typography;

// ============================================================================
// Types
// ============================================================================

export interface Activity {
	id: string;
	action: string;
	details?: string;
	timestamp: string;
	user?: {
		id: string;
		name: string;
	};
}

export interface ActivityTimelineProps {
	/** Array of activities to display */
	activities: Activity[];
	/** Whether the activities are currently loading */
	isLoading?: boolean;
}

// ============================================================================
// Helpers
// ============================================================================

/**
 * Format a timestamp for display.
 */
function formatTimestamp(dateString: string, justNowLabel: string): string {
	const date = new Date(dateString);
	const now = new Date();
	const diffMs = now.getTime() - date.getTime();
	const diffMins = Math.floor(diffMs / 60000);
	const diffHours = Math.floor(diffMs / 3600000);
	const diffDays = Math.floor(diffMs / 86400000);

	if (diffMins < 1) return justNowLabel;
	if (diffMins < 60) return `vor ${diffMins} Min.`;
	if (diffHours < 24) return `vor ${diffHours} Std.`;
	if (diffDays < 7) return `vor ${diffDays} Tagen`;

	return date.toLocaleDateString("de-CH", {
		day: "2-digit",
		month: "2-digit",
		year: "numeric",
		hour: "2-digit",
		minute: "2-digit",
	});
}

// ============================================================================
// Component
// ============================================================================

export function ActivityTimeline({ activities, isLoading = false }: ActivityTimelineProps) {
	const { t } = useTranslation();

	if (isLoading) {
		return <Skeleton active paragraph={{ rows: 4 }} />;
	}

	if (activities.length === 0) {
		return (
			<Empty description={t("common:message.noActivity")} image={Empty.PRESENTED_IMAGE_SIMPLE} />
		);
	}

	const justNowLabel = t("common:label.justNow");

	return (
		<Timeline
			items={activities.map((activity) => ({
				key: activity.id,
				children: (
					<div>
						<Text strong>{activity.action}</Text>
						<br />
						<Text type="secondary" style={{ fontSize: 12 }}>
							{activity.user?.name && `${activity.user.name} • `}
							{formatTimestamp(activity.timestamp, justNowLabel)}
						</Text>
						{activity.details && (
							<Paragraph type="secondary" style={{ marginTop: 4, marginBottom: 0, fontSize: 13 }}>
								{activity.details}
							</Paragraph>
						)}
					</div>
				),
			}))}
		/>
	);
}
