import { createEntityApi } from "../../common/api/entityApi";
import { api, getApiUrl, getRestUrl } from "../../common/api/client";
import type { Building, BuildingListItem, ProjectionResult } from "./types";

// Geocoding types
interface GeocodeRequest {
	street?: string;
	zip?: string;
	city?: string;
	country?: string;
	geoAddress?: string;
}

interface GeocodeResponse {
	geoCoordinates: string;
	geoZoom: number;
}

/**
 * Geocode an address to get coordinates.
 * Returns null if geocoding fails.
 */
export async function geocodeAddress(request: GeocodeRequest): Promise<GeocodeResponse | null> {
	try {
		const response = await api.post<GeocodeResponse>(
			getApiUrl("building", "buildings/location"),
			request
		);
		return response.data;
	} catch {
		return null;
	}
}

export const buildingApi = createEntityApi<Building>({
	module: "building",
	path: "buildings",
	type: "building",
	includes: "include[building]=account,contacts,coverFoto",
	relations: {
		account: "account",
		contacts: "contact",
		coverFoto: "document",
	},
});

export const buildingListApi = createEntityApi<BuildingListItem>({
	module: "building",
	path: "buildings",
	type: "building",
	includes: "",
	relations: {},
});

/**
 * Fetch projection data for a building.
 * Returns the projection result with periods and elements for chart display.
 */
export async function fetchProjection(buildingId: string): Promise<ProjectionResult> {
	const response = await api.get<ProjectionResult>(
		getRestUrl("building", `buildings/${buildingId}/projection`)
	);
	return response.data;
}
