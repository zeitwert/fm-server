import { useQuery } from "@tanstack/react-query";
import { api, getApiUrl } from "../api/client";
import type { Enumerated } from "../types";

interface UseAggregateSearchOptions {
	/** Minimum search text length to trigger query (default: 2) */
	minLength?: number;
	/** Cache stale time in ms (default: 5 min) */
	staleTime?: number;
}

/**
 * Search aggregates (entities) with text filter for autocomplete.
 *
 * Replaces `AggregateSource` from fm-ui. Useful for entity references
 * where you need to search by name (e.g., contacts, users).
 *
 * @param entityType - Entity type, e.g., "contact", "user"
 * @param searchText - Search text (query disabled if shorter than minLength)
 * @param module - API module (defaults to entityType)
 * @param options - Query options
 *
 * @example
 * const [searchText, setSearchText] = useState('');
 * const debouncedSearch = useDebouncedValue(searchText, 200);
 * const { data: contacts } = useAggregateSearch('contact', debouncedSearch);
 */
export function useAggregateSearch(
	entityType: string,
	searchText: string,
	module?: string,
	options?: UseAggregateSearchOptions
) {
	const resolvedModule = module ?? entityType;
	const minLength = options?.minLength ?? 2;

	return useQuery({
		queryKey: ["aggregateSearch", entityType, searchText],
		queryFn: async (): Promise<Enumerated[]> => {
			const url = getApiUrl(
				resolvedModule,
				`${entityType}s?filter[searchText]=${encodeURIComponent(searchText)}`
			);
			const response = await api.get(url);

			// Handle JSONAPI response format
			// eslint-disable-next-line @typescript-eslint/no-explicit-any
			const data = response.data as any;

			if (data?.data && Array.isArray(data.data)) {
				// JSONAPI format: { data: [{ id, type, attributes }] }
				return data.data.map(
					(item: {
						id: string;
						type?: string;
						attributes?: { caption?: string; name?: string };
					}) => ({
						id: item.id,
						name: item.attributes?.caption ?? item.attributes?.name ?? item.id,
						itemType: item.type ? { id: item.type, name: item.type } : undefined,
					})
				);
			}

			// Direct array format (simple endpoint)
			if (Array.isArray(data)) {
				return data.map(
					(item: { id: string; name?: string; caption?: string; itemType?: string }) => ({
						id: item.id,
						name: item.caption ?? item.name ?? item.id,
						itemType: item.itemType ? { id: item.itemType, name: item.itemType } : undefined,
					})
				);
			}

			return [];
		},
		enabled: searchText.length >= minLength,
		staleTime: options?.staleTime ?? 1000 * 60 * 5, // 5 min
	});
}
