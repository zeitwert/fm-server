import { useQuery } from '@tanstack/react-query';
import { api, getApiUrl } from '../../../common/api/client';

interface BuildingAttributes {
	name?: string;
	street?: string;
	zip?: string;
	city?: string;
	geoCoordinates?: string;
}

interface BuildingApiItem {
	id: string;
	attributes?: BuildingAttributes;
}

interface BuildingApiResponse {
	data?: BuildingApiItem[];
}

export interface BuildingInfo {
	id: string;
	name: string;
	address: string;
	lat: number;
	lng: number;
}

function parseBuilding(item: BuildingApiItem): BuildingInfo | null {
	const coords = item.attributes?.geoCoordinates;
	if (!coords?.startsWith('WGS:')) {
		return null;
	}

	const [latRaw, lngRaw] = coords.substring(4).split(',');
	const lat = Number.parseFloat(latRaw ?? '');
	const lng = Number.parseFloat(lngRaw ?? '');

	if (Number.isNaN(lat) || Number.isNaN(lng)) {
		return null;
	}

	const addressParts = [
		item.attributes?.street,
		[item.attributes?.zip, item.attributes?.city].filter(Boolean).join(' '),
	]
		.filter(Boolean)
		.join(', ');

	return {
		id: item.id,
		name: item.attributes?.name ?? 'Unbekannt',
		address: addressParts,
		lat,
		lng,
	};
}

async function fetchBuildings() {
	const response = await api.get<BuildingApiResponse>(getApiUrl('building', 'buildings'));
	const items = response.data?.data ?? [];
	const buildings = items.map(parseBuilding).filter((item): item is BuildingInfo => item !== null);

	return {
		totalCount: items.length,
		buildings,
	};
}

export function useHomeMapBuildings(accountId?: string | null) {
	return useQuery({
		queryKey: ['home', 'map', 'buildings', accountId],
		queryFn: fetchBuildings,
		enabled: Boolean(accountId),
	});
}
