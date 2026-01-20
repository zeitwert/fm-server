import { useQuery } from "@tanstack/react-query";
import { api, getEnumUrl } from "../api/client";
import type { Enumerated } from "../types";

interface UseCodeTableOptions {
	/** Whether the query is enabled (default: true) */
	enabled?: boolean;
	/** Cache stale time in ms (default: 30 min) */
	staleTime?: number;
}

/**
 * Load code table (enumerated values) from server.
 *
 * Replaces `EnumeratedSource` from fm-ui with TanStack Query.
 * Code tables are cached for 30 minutes by default since they rarely change.
 *
 * @param source - Code table path in format "module/enumName", e.g., "building/codeBuildingType" or "oe/codeCountry"
 * @param options - Query options
 *
 * @example
 * const { data: countries } = useCodeTable('oe/codeCountry');
 * const { data: buildingTypes } = useCodeTable('building/codeBuildingType');
 */
export function useCodeTable(source: string, options?: UseCodeTableOptions) {
	const [module, enumName] = source.split("/");

	return useQuery({
		queryKey: ["codeTable", source],
		queryFn: async (): Promise<Enumerated[]> => {
			if (!module || !enumName) return [];
			const response = await api.get<Enumerated[]>(getEnumUrl(module, enumName));
			return response.data;
		},
		staleTime: options?.staleTime ?? 1000 * 60 * 30, // 30 min default
		enabled: (options?.enabled ?? true) && !!source && source.includes("/"),
	});
}
