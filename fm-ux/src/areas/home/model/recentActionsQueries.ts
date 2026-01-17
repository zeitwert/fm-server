import { useQuery } from '@tanstack/react-query';
import { api, getRestUrl } from '../../../common/api/client';
import type { Enumerated } from '../../../session/model/types';

interface ActionItemType {
	id: string;
	name?: string;
}

interface ActionItem {
	id: string;
	name: string;
	itemType?: ActionItemType;
}

export interface RecentAction {
	item: ActionItem;
	seqNr: number;
	timestamp: string;
	user: Enumerated;
	changes?: unknown;
	oldCaseStage?: Enumerated;
	newCaseStage?: Enumerated;
}

async function fetchRecentActions(accountId: string) {
	const response = await api.get<RecentAction[]>(
		getRestUrl('home', `recentActions/${accountId}`)
	);
	return response.data ?? [];
}

export function useHomeRecentActions(accountId?: string | null) {
	return useQuery({
		queryKey: ['home', 'recent-actions', accountId],
		queryFn: () => fetchRecentActions(accountId ?? ''),
		enabled: Boolean(accountId),
	});
}
