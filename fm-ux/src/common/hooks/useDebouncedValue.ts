import { useEffect, useState } from "react";

/**
 * Debounce a value by delaying updates until after a specified delay.
 *
 * Useful for search inputs where you want to wait for the user to stop typing
 * before triggering an API call.
 *
 * @param value - The value to debounce
 * @param delay - Delay in milliseconds (default: 200ms)
 * @returns The debounced value
 *
 * @example
 * const [searchText, setSearchText] = useState('');
 * const debouncedSearch = useDebouncedValue(searchText, 300);
 *
 * // debouncedSearch only updates 300ms after searchText stops changing
 * const { data } = useQuery({
 *   queryKey: ['search', debouncedSearch],
 *   queryFn: () => searchApi(debouncedSearch),
 *   enabled: debouncedSearch.length >= 2,
 * });
 */
export function useDebouncedValue<T>(value: T, delay: number = 200): T {
	const [debouncedValue, setDebouncedValue] = useState<T>(value);

	useEffect(() => {
		const timer = setTimeout(() => {
			setDebouncedValue(value);
		}, delay);

		return () => {
			clearTimeout(timer);
		};
	}, [value, delay]);

	return debouncedValue;
}
