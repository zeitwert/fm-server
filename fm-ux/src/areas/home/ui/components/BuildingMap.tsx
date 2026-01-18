import GoogleMapReact from "google-map-react";
import { useEffect, useMemo, useRef, useState } from "react";
import type { CSSProperties } from "react";
import type { BuildingInfo } from "../../model";

const GOOGLE_API_KEY = "AIzaSyBQF6Fi_Z0tZxVh5Eqzfx2m7hK3n718jsI";
const DEFAULT_ZOOM = 16;
const DEFAULT_DIMENSIONS = { width: 800, height: 600 };

interface Position {
	lat: number;
	lng: number;
}

export interface BuildingMapProps {
	buildings: BuildingInfo[];
	zoom?: number;
	onZoomChange?: (zoom: number) => void;
	onClick?: (building: BuildingInfo) => void;
}

export function BuildingMap({ buildings, zoom, onZoomChange, onClick }: BuildingMapProps) {
	const containerRef = useRef<HTMLDivElement | null>(null);
	const [dimensions, setDimensions] = useState(DEFAULT_DIMENSIONS);

	useEffect(() => {
		const element = containerRef.current;
		if (!element) {
			return;
		}

		const updateDimensions = () => {
			const rect = element.getBoundingClientRect();
			if (rect.width > 0 && rect.height > 0) {
				setDimensions({ width: rect.width, height: rect.height });
			}
		};

		updateDimensions();
		const observer = new ResizeObserver((entries) => {
			const entry = entries[0];
			if (entry) {
				setDimensions({ width: entry.contentRect.width, height: entry.contentRect.height });
			}
		});

		observer.observe(element);
		return () => observer.disconnect();
	}, []);

	const mapConfig = useMemo(() => {
		if (buildings.length === 0) {
			return null;
		}

		if (buildings.length === 1) {
			const [building] = buildings;
			return {
				center: { lat: building.lat, lng: building.lng },
				zoom: zoom ?? DEFAULT_ZOOM,
			};
		}

		const minLat = buildings.reduce((min, building) => Math.min(min, building.lat), 180);
		const minLng = buildings.reduce((min, building) => Math.min(min, building.lng), 180);
		const maxLat = buildings.reduce((max, building) => Math.max(max, building.lat), -180);
		const maxLng = buildings.reduce((max, building) => Math.max(max, building.lng), -180);

		const boundsZoom = getBoundsZoomLevel(
			{ lat: minLat, lng: minLng },
			{ lat: maxLat, lng: maxLng },
			dimensions
		);

		return {
			center: {
				lat: minLat + (maxLat - minLat) / 2,
				lng: minLng + (maxLng - minLng) / 2,
			},
			zoom: Number.isFinite(boundsZoom) ? boundsZoom : DEFAULT_ZOOM,
		};
	}, [buildings, dimensions, zoom]);

	if (!mapConfig) {
		return null;
	}

	return (
		<div
			ref={containerRef}
			style={{ width: "100%", height: "100%", borderRadius: 0, overflow: "hidden" }}
		>
			<GoogleMapReact
				bootstrapURLKeys={{ key: GOOGLE_API_KEY }}
				center={mapConfig.center}
				zoom={mapConfig.zoom}
				onZoomAnimationStart={onZoomChange}
			>
				{buildings.map((building) => (
					<MarkerWithHover
						key={building.id}
						text={building.name}
						lat={building.lat}
						lng={building.lng}
						onClick={() => onClick?.(building)}
					/>
				))}
			</GoogleMapReact>
		</div>
	);
}

function getBoundsZoomLevel(sw: Position, ne: Position, mapDim: { width: number; height: number }) {
	const MAX_ZOOM = 18;
	const WORLD_DIM = { height: 256, width: 256 };
	const latFraction = (latRad(ne.lat) - latRad(sw.lat)) / Math.PI;

	const lngDiff = ne.lng - sw.lng;
	const lngFraction = (lngDiff < 0 ? lngDiff + 360 : lngDiff) / 360;

	const latZoom = zoom(mapDim.height, WORLD_DIM.height, latFraction);
	const lngZoom = zoom(mapDim.width, WORLD_DIM.width, lngFraction);

	return Math.min(latZoom, lngZoom, MAX_ZOOM);

	function zoom(mapPx: number, worldPx: number, fraction: number) {
		return Math.floor(Math.log(mapPx / worldPx / fraction) / Math.LN2);
	}

	function latRad(lat: number) {
		const sin = Math.sin((lat * Math.PI) / 180);
		const radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
		return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
	}
}

const DEFAULT_MARKER_SIZE = 20;
const HOVER_MARKER_SIZE = 40;

const DEFAULT_MARKER_STYLE: CSSProperties = {
	position: "absolute",
	width: DEFAULT_MARKER_SIZE,
	height: DEFAULT_MARKER_SIZE,
	left: -DEFAULT_MARKER_SIZE / 2,
	top: -DEFAULT_MARKER_SIZE / 2,
	backgroundColor: "#bbb",
	border: "2px solid #888",
	borderRadius: "50%",
	cursor: "pointer",
};

const HOVER_MARKER_STYLE: CSSProperties = {
	...DEFAULT_MARKER_STYLE,
	width: HOVER_MARKER_SIZE,
	height: HOVER_MARKER_SIZE,
	left: -HOVER_MARKER_SIZE / 2,
	top: -HOVER_MARKER_SIZE / 2,
	zIndex: 1000,
	backgroundColor: "#add8e6",
	border: "4px solid #3f51b5",
};

const DEFAULT_TEXT_STYLE: CSSProperties = {
	position: "absolute",
	backgroundColor: "rgba(255, 255, 255, .6)",
	fontSize: 16,
	color: "#000",
	left: "40px",
	top: "6px",
	width: "200px",
	textAlign: "left",
};

interface MarkerWithHoverProps {
	$hover?: boolean;
	text: string;
	lat: number;
	lng: number;
	onClick?: () => void;
}

function MarkerWithHover({ $hover, text, onClick }: MarkerWithHoverProps) {
	const markerStyle = $hover ? HOVER_MARKER_STYLE : DEFAULT_MARKER_STYLE;

	return (
		<div style={markerStyle} onClick={onClick}>
			{$hover && <div style={DEFAULT_TEXT_STYLE}>{text}</div>}
		</div>
	);
}
