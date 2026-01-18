import { Empty, Spin, Timeline, Typography } from "antd";
import { useTranslation } from "react-i18next";
import { DashboardCard } from "./components/DashboardCard";
import { useHomeRecentActions } from "../model";
import { useSessionStore } from "../../../session/model/sessionStore";
import type { RecentAction } from "../model";

const RELATIVE_FORMAT = new Intl.RelativeTimeFormat("de-DE", {
	numeric: "auto",
	style: "narrow",
});

function formatRelativeTime(date: Date) {
	const diffMs = date.getTime() - Date.now();
	const diffMinutes = Math.round(diffMs / 60000);

	if (Math.abs(diffMinutes) < 60) {
		return RELATIVE_FORMAT.format(diffMinutes, "minute");
	}

	const diffHours = Math.round(diffMinutes / 60);
	if (Math.abs(diffHours) < 24) {
		return RELATIVE_FORMAT.format(diffHours, "hour");
	}

	const diffDays = Math.round(diffHours / 24);
	if (Math.abs(diffDays) < 7) {
		return RELATIVE_FORMAT.format(diffDays, "day");
	}

	const diffWeeks = Math.round(diffDays / 7);
	if (Math.abs(diffWeeks) < 5) {
		return RELATIVE_FORMAT.format(diffWeeks, "week");
	}

	const diffMonths = Math.round(diffDays / 30);
	if (Math.abs(diffMonths) < 12) {
		return RELATIVE_FORMAT.format(diffMonths, "month");
	}

	const diffYears = Math.round(diffDays / 365);
	return RELATIVE_FORMAT.format(diffYears, "year");
}

function getItemPath(action: RecentAction) {
	const typeId = action.item.itemType?.id;
	if (!typeId) return null;
	const typeSegment = typeId.length > 4 ? typeId.substring(4) : typeId;
	return `/${typeSegment}/${action.item.id}`;
}

function ActivityRow({
	action,
	sessionUserId,
	youLabel,
	openingLabel,
	modificationLabel,
	statusChangeLabel,
}: {
	action: RecentAction;
	sessionUserId?: string;
	youLabel: string;
	openingLabel: string;
	modificationLabel: string;
	statusChangeLabel: string;
}) {
	const timestamp = new Date(action.timestamp);
	const timestampLabel = Number.isNaN(timestamp.getTime())
		? action.timestamp
		: formatRelativeTime(timestamp);
	const userLabel = action.user.id === sessionUserId ? youLabel : action.user.name;

	// Determine activity type
	const getActivityName = () => {
		const typeId = action.item.itemType?.id ?? "";
		if (typeId.startsWith("obj")) {
			return action.seqNr === 0 ? openingLabel : modificationLabel;
		}

		if (action.seqNr === 0) {
			return openingLabel;
		}

		if (action.newCaseStage?.id === action.oldCaseStage?.id) {
			return modificationLabel;
		}

		return statusChangeLabel;
	};

	const activityLabel = `${userLabel} · ${getActivityName()}`;
	const href = getItemPath(action);

	return (
		<div style={{ display: "flex", flexDirection: "column", gap: 4 }}>
			<div style={{ display: "flex", justifyContent: "space-between", gap: 8 }}>
				<Typography.Text strong>
					{href ? <a href={href}>{action.item.name}</a> : action.item.name}
				</Typography.Text>
				<Typography.Text type="secondary" style={{ whiteSpace: "nowrap" }}>
					{timestampLabel}
				</Typography.Text>
			</div>
			<Typography.Text type="secondary">{activityLabel}</Typography.Text>
		</div>
	);
}

export function HomeCardRecentActions() {
	const { t } = useTranslation("home");
	const { t: tCommon } = useTranslation("common");
	const accountId = useSessionStore((state) => state.sessionInfo?.account?.id);
	const sessionUserId = useSessionStore((state) => state.sessionInfo?.user.id);
	const { data, isLoading } = useHomeRecentActions(accountId);
	const actions = data ?? [];

	return (
		<DashboardCard title={t("recentActions")}>
			<div style={{ height: "100%", position: "relative", padding: 12 }}>
				{isLoading && (
					<div
						style={{
							position: "absolute",
							inset: 0,
							display: "flex",
							alignItems: "center",
							justifyContent: "center",
						}}
					>
						<Spin />
					</div>
				)}
				{!isLoading && actions.length === 0 && <Empty description={t("noRecentActions")} />}
				{!isLoading && actions.length > 0 && (
					<Timeline
						style={{ marginTop: 4 }}
						items={actions.map((action, index) => ({
							key: `${action.item.id}-${action.seqNr}-${index}`,
							children: (
								<ActivityRow
									action={action}
									sessionUserId={sessionUserId}
									youLabel={tCommon("you")}
									openingLabel={t("opening")}
									modificationLabel={t("modification")}
									statusChangeLabel={t("statusChange")}
								/>
							),
						}))}
					/>
				)}
			</div>
		</DashboardCard>
	);
}
