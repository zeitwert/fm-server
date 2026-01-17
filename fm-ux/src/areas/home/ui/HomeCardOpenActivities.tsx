import { Avatar, Collapse, Divider, Empty, Spin, Typography } from 'antd';
import { DashboardCard } from './components/DashboardCard';
import { useHomeOpenActivities } from '../model';
import { useSessionStore } from '../../../session/model/sessionStore';
import type { OpenActivity } from '../model';
import type { TypedEnumerated } from '../../../session/model/types';

const DATE_FORMAT = new Intl.DateTimeFormat('de-DE', {
	day: '2-digit',
	month: '2-digit',
	year: 'numeric',
});
const RELATIVE_FORMAT = new Intl.RelativeTimeFormat('de-DE', { numeric: 'auto' });

function formatDate(date: Date) {
	return DATE_FORMAT.format(date);
}

function formatRelativeTime(date: Date) {
	const diffMs = date.getTime() - Date.now();
	const diffMinutes = Math.round(diffMs / 60000);

	if (Math.abs(diffMinutes) < 60) {
		return RELATIVE_FORMAT.format(diffMinutes, 'minute');
	}

	const diffHours = Math.round(diffMinutes / 60);
	if (Math.abs(diffHours) < 24) {
		return RELATIVE_FORMAT.format(diffHours, 'hour');
	}

	const diffDays = Math.round(diffHours / 24);
	if (Math.abs(diffDays) < 7) {
		return RELATIVE_FORMAT.format(diffDays, 'day');
	}

	const diffWeeks = Math.round(diffDays / 7);
	if (Math.abs(diffWeeks) < 5) {
		return RELATIVE_FORMAT.format(diffWeeks, 'week');
	}

	const diffMonths = Math.round(diffDays / 30);
	if (Math.abs(diffMonths) < 12) {
		return RELATIVE_FORMAT.format(diffMonths, 'month');
	}

	const diffYears = Math.round(diffDays / 365);
	return RELATIVE_FORMAT.format(diffYears, 'year');
}

function getInitials(name: string) {
	const parts = name.trim().split(/\s+/).filter(Boolean);
	if (parts.length === 0) return '??';
	if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
	return `${parts[0][0]}${parts[1][0]}`.toUpperCase();
}

function getItemPath(item?: TypedEnumerated) {
	if (!item?.itemType?.id || !item.id) return null;
	const typeId = item.itemType.id;
	const typeSegment = typeId.length > 4 ? typeId.substring(4) : typeId;
	return `/${typeSegment}/${item.id}`;
}

function isActivityBuilding(activity: OpenActivity): boolean {
	const typeId = activity.item.itemType?.id ?? '';
	// itemType.id format: "AGGR" prefix + type name, or just the type name
	const typeSegment = typeId.length > 4 ? typeId.substring(4) : typeId;
	return typeSegment.toLowerCase() === 'building';
}

function renderLink(label: string, href: string | null) {
	if (!href) {
		return <span>{label}</span>;
	}
	return <a href={href}>{label}</a>;
}

function ActivityList({
	activities,
	sessionUserId,
}: {
	activities: OpenActivity[];
	sessionUserId?: string;
}) {
	return (
		<div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
			{activities.map((activity, index) => {
				const userName = activity.user?.name ?? '??';
				const userLabel = activity.user?.id === sessionUserId ? 'Du' : userName;
				const initials = getInitials(userName);
				const dueAt = activity.dueAt ? formatDate(activity.dueAt) : 'Unbekannt';
				const dueRelative = activity.dueAt ? formatRelativeTime(activity.dueAt) : null;
				const isOverdue = activity.dueAt ? activity.dueAt.getTime() <= Date.now() : true;
				const relatedToPath = getItemPath(activity.relatedTo);
				const itemPath = getItemPath(activity.item);
				const subject = activity.subject || '(ohne Titel)';

				// If the activity itself is a building, show only the building as header
				const isBuilding = isActivityBuilding(activity);
				const headerName = isBuilding
					? (activity.item.name ?? 'Unbekannt')
					: (activity.relatedTo?.name ?? 'Unbekannt');
				const headerPath = isBuilding ? itemPath : relatedToPath;

				return (
					<div key={`${activity.item.id}-${index}`}>
						<div style={{ display: 'flex', gap: 12 }}>
							<Avatar size="large" style={{ backgroundColor: '#1677ff' }}>
								{initials}
							</Avatar>
							<div style={{ flex: 1 }}>
								<div
									style={{
										display: 'flex',
										justifyContent: 'space-between',
										gap: 8,
									}}
								>
									<Typography.Text strong>
										{renderLink(headerName, headerPath)}
									</Typography.Text>
									{dueRelative && (
										<Typography.Text type={isOverdue ? 'danger' : 'secondary'}>
											{dueRelative}
										</Typography.Text>
									)}
								</div>
								<Typography.Text type="secondary" style={{ fontSize: 12 }}>
									{dueAt} · {userLabel}
								</Typography.Text>
								{!isBuilding && (
									<div style={{ marginTop: 4 }}>
										<Typography.Text strong>
											{renderLink(subject, itemPath)}
										</Typography.Text>
									</div>
								)}
								{activity.content && (
									<Typography.Paragraph
										style={{ marginTop: 4, marginBottom: 0 }}
										type="secondary"
									>
										{activity.content}
									</Typography.Paragraph>
								)}
							</div>
						</div>
						{index < activities.length - 1 && <Divider style={{ margin: '12px 0 0' }} />}
					</div>
				);
			})}
		</div>
	);
}

export function HomeCardOpenActivities() {
	const accountId = useSessionStore((state) => state.sessionInfo?.account?.id);
	const sessionUserId = useSessionStore((state) => state.sessionInfo?.user.id);
	const { data, isLoading } = useHomeOpenActivities(accountId);
	const activities = data?.activities ?? [];

	const now = Date.now();
	const futureActivities = activities.filter((activity) => activity.dueAt && activity.dueAt.getTime() > now);
	const overdueActivities = activities.filter(
		(activity) => !activity.dueAt || activity.dueAt.getTime() <= now
	);

	return (
		<DashboardCard title={`Laufende Aktivitäten (${data?.totalCount ?? 0})`}>
			<div style={{ height: '100%', position: 'relative' }}>
				{isLoading && (
					<div
						style={{
							position: 'absolute',
							inset: 0,
							display: 'flex',
							alignItems: 'center',
							justifyContent: 'center',
						}}
					>
						<Spin />
					</div>
				)}
				{!isLoading && activities.length === 0 && (
					<Empty description="Keine laufenden Aktivitäten." />
				)}
				{!isLoading && activities.length > 0 && (
					<div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
						{futureActivities.length > 0 && (
							<ActivityList activities={futureActivities} sessionUserId={sessionUserId} />
						)}
						{overdueActivities.length > 0 && (
							<Collapse
								size="small"
								ghost
								items={[
									{
										key: 'overdue',
										label: `Überfällig (${overdueActivities.length})`,
										children: (
											<ActivityList
												activities={overdueActivities}
												sessionUserId={sessionUserId}
											/>
										),
									},
								]}
							/>
						)}
					</div>
				)}
			</div>
		</DashboardCard>
	);
}
