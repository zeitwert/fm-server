import { useQuery } from "@tanstack/react-query";
import { api, getRestUrl } from "../../../common/api/client";
import type { HomeOverview } from "./types";

async function fetchOverview(accountId: string) {
	const response = await api.get<HomeOverview>(getRestUrl("home", `overview/${accountId}`));
	return response.data;
}

export function useHomeOverview(accountId?: string | null) {
	return useQuery({
		queryKey: ["home", "overview", accountId],
		queryFn: () => fetchOverview(accountId ?? ""),
		enabled: Boolean(accountId),
	});
}
