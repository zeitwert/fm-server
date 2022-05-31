
export interface GeocodeRequest {
	geoAddress: string;
	street: string;
	zip: string;
	city: string;
	country: string;
}

export interface GeocodeResponse {
	geoCoordinates: string;
	geoZoom: number;
}

