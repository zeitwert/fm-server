import { useQuery } from "@tanstack/react-query";
import { api, getEnumUrl } from "../api/client";
import type { Enumerated } from "../types";

interface UseDependentCodeTableOptions {
	/** Cache stale time in ms (default: 30 min) */
	staleTime?: number;
}

/**
 * Load code table that depends on a parent value.
 *
 * The query is automatically disabled when parentValue is undefined/null,
 * and re-fetches when parentValue changes.
 *
 * @param source - Full path including parent ID, e.g., "building/codeBuildingSubType/123"
 * @param parentValue - The parent value ID (query disabled if undefined/null)
 * @param options - Query options
 *
 * @example
 * const buildingTypeId = watch('buildingType')?.id;
 * const { data: subTypes } = useDependentCodeTable(
 *   `building/codeBuildingSubType/${buildingTypeId}`,
 *   buildingTypeId
 * );
 */
export function useDependentCodeTable(
	source: string,
	parentValue: string | undefined | null,
	options?: UseDependentCodeTableOptions
) {
	const [module, ...rest] = source.split("/");
	const enumPath = rest.join("/");

	return useQuery({
		queryKey: ["codeTable", source, parentValue],
		queryFn: async (): Promise<Enumerated[]> => {
			if (!parentValue || !module || !enumPath) return [];
			const response = await api.get<Enumerated[]>(getEnumUrl(module, enumPath));
			return response.data;
		},
		staleTime: options?.staleTime ?? 1000 * 60 * 30,
		enabled: !!parentValue && !!source,
	});
}
