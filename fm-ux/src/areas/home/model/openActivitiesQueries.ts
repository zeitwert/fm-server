import { useQuery } from "@tanstack/react-query";
import { api, getRestUrl } from "../../../common/api/client";
import type { Enumerated, TypedEnumerated } from "@/session/model/types";

interface OpenActivityApiItem {
	item: TypedEnumerated;
	relatedTo: TypedEnumerated;
	owner: Enumerated;
	user?: Enumerated;
	dueAt?: string;
	subject?: string;
	content?: string;
	priority?: Enumerated;
}

export interface OpenActivity {
	item: TypedEnumerated;
	relatedTo: TypedEnumerated;
	owner: Enumerated;
	user?: Enumerated;
	dueAt: Date | null;
	subject: string;
	content: string;
	priority?: Enumerated;
}

function parseActivity(activity: OpenActivityApiItem): OpenActivity {
	const dueAt = activity.dueAt ? new Date(activity.dueAt) : null;
	const parsedDueAt = dueAt && !Number.isNaN(dueAt.getTime()) ? dueAt : null;

	return {
		item: activity.item,
		relatedTo: activity.relatedTo,
		owner: activity.owner,
		user: activity.user,
		dueAt: parsedDueAt,
		subject: activity.subject ?? "",
		content: activity.content ?? "",
		priority: activity.priority,
	};
}

async function fetchOpenActivities(accountId: string) {
	const response = await api.get<OpenActivityApiItem[]>(
		getRestUrl("home", `openActivities/${accountId}`)
	);
	const activities = (response.data ?? [])
		.map(parseActivity)
		.sort((a, b) => (b.dueAt?.getTime() ?? 0) - (a.dueAt?.getTime() ?? 0));

	return {
		activities,
		totalCount: activities.length,
	};
}

export function useHomeOpenActivities(accountId?: string | null) {
	return useQuery({
		queryKey: ["home", "open-activities", accountId],
		queryFn: () => fetchOpenActivities(accountId ?? ""),
		enabled: Boolean(accountId),
	});
}
