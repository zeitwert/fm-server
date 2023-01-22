
import GoogleMapReact from "google-map-react";
import { observer } from "mobx-react";
import React from "react";

const GOOGLE_API_KEY = "AIzaSyBQF6Fi_Z0tZxVh5Eqzfx2m7hK3n718jsI";

export interface BuildingInfo {
	id: string;
	name: string;
	address: string;
	lat: number;
	lng: number;
}

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

@observer
export default class BuildingMap extends React.Component<BuildingMapProps> {

	render() {
		const buildings = this.props.buildings;
		if (buildings.length === 0) {
			return <div><p>Keine Immobilien ausgewählt oder keine Koordinaten berechnet.</p></div>;
		} else if (buildings.length === 1) {
			const { name, lat, lng } = buildings[0];
			const { zoom } = this.props;
			return (
				<GoogleMapReact
					bootstrapURLKeys={{ key: GOOGLE_API_KEY }}
					defaultCenter={{ lat: lat, lng: lng }}
					defaultZoom={zoom ? zoom : 16}
					onZoomAnimationStart={this.onZoom}
				>
					<MarkerWithHover text={name} lat={lat} lng={lng} onClick={() => this.props.onClick?.(buildings[0])} />
				</GoogleMapReact>
			);
		} else if (buildings.length > 1) {
			const minLat = buildings.reduce((min, b) => Math.min(min, b.lat), 180);
			const minLng = buildings.reduce((min, b) => Math.min(min, b.lng), 180);
			const maxLat = buildings.reduce((max, b) => Math.max(max, b.lat), 0);
			const maxLng = buildings.reduce((max, b) => Math.max(max, b.lng), 0);
			const zoom = this.getBoundsZoomLevel({ lat: minLat, lng: minLng }, { lat: maxLat, lng: maxLng }, { width: 800, height: 800 });
			const centerLat = minLat + (maxLat - minLat) / 2;
			const centerLng = minLng + (maxLng - minLng) / 2;
			return (
				<GoogleMapReact
					bootstrapURLKeys={{ key: GOOGLE_API_KEY }}
					defaultCenter={{ lat: centerLat, lng: centerLng }}
					defaultZoom={zoom ? zoom : 7}
					onZoomAnimationStart={this.onZoom}
				>
					{
						buildings.map(
							(b, index) => <MarkerWithHover key={"marker-" + index} text={b.name} lat={b.lat} lng={b.lng} onClick={() => this.props.onClick?.(b)} />
						)
					}
				</GoogleMapReact>
			);
		}
	}

	private onZoom = (zoom: number): void => {
		this.props.onZoomChange?.(zoom);
	}

	// see https://stackoverflow.com/questions/6048975/google-maps-v3-how-to-calculate-the-zoom-level-for-a-given-bounds
	private getBoundsZoomLevel = (sw: Position, ne: Position, mapDim: { width: number, height: number }): number => {

		const MAX_ZOOM = 18;
		const WORLD_DIM = { height: 256, width: 256 };

		const latFraction = (latRad(ne.lat) - latRad(sw.lat)) / Math.PI;

		const lngDiff = ne.lng - sw.lng;
		const lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;

		const latZoom = zoom(mapDim.height, WORLD_DIM.height, latFraction);
		const lngZoom = zoom(mapDim.width, WORLD_DIM.width, lngFraction);

		return Math.min(latZoom, lngZoom, MAX_ZOOM);

		function zoom(mapPx: number, worldPx: number, fraction: number) {
			return Math.floor(Math.log(mapPx / worldPx / fraction) / Math.LN2);
		}

		function latRad(lat: number) {
			const sin = Math.sin(lat * Math.PI / 180);
			const radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
			return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
		}

	}


};

/*
const K_SIZE = 40;

const xDefaultMarkerStyle = {
	// initially any map object has left top corner at lat lng coordinates
	// it's on you to set object origin to 0,0 coordinates
	position: "absolute",
	width: K_SIZE,
	height: K_SIZE,
	left: -K_SIZE / 2,
	top: -K_SIZE / 2,
	border: "5px solid #f44336",
	borderRadius: K_SIZE,
	backgroundColor: "white",
	textAlign: "center",
	color: "#3f51b5",
	fontSize: 16,
	fontWeight: "bold",
	padding: 4,
	cursor: "pointer"
};

const xHoverMarkerStyle = {
	...xDefaultMarkerStyle,
	border: "5px solid #3f51b5",
	color: "#f44336"
};
*/

const DefaultSize = 20;

const DefaultMarkerStyle = {
	// initially any map object has left top corner at lat lng coordinates
	// it's on you to set object origin to 0,0 coordinates
	position: "absolute",
	width: DefaultSize,
	height: DefaultSize,
	left: -DefaultSize / 2,
	top: -DefaultSize / 2,
	backgroundColor: "#bbb",
	border: "2px solid #888",
	borderRadius: "50%",
	//backgroundColor: "white",
	cursor: "pointer"
};

const HoverSize = 40;

const HoverMarkerStyle = {
	...DefaultMarkerStyle,
	width: HoverSize,
	height: HoverSize,
	left: -HoverSize / 2,
	top: -HoverSize / 2,
	zIndex: 1000,
	backgroundColor: "#add8e6",
	border: "4px solid #3f51b5"
};

const DefaultTextStyle = {
	position: "absolute",
	backgroundColor: "rgba(255, 255, 255, .6)",
	fontSize: 16,
	color: "#000",
	left: "40px",
	top: "6px",
	width: "200px",
	textAlign: "left"
};


interface MarkerWithHoverProps {
	$hover?: boolean;
	text: string;
	lat: number;
	lng: number;
	onClick?: () => void;
}

class MarkerWithHover extends React.Component<MarkerWithHoverProps> {

	//shouldComponentUpdate = shouldPureComponentUpdate;

	render() {
		const markerStyle: any = this.props.$hover ? HoverMarkerStyle : DefaultMarkerStyle;
		const textStyle: any = DefaultTextStyle;
		return (
			<div style={markerStyle} onClick={() => this.props.onClick?.()}>
				{this.props.$hover && <div style={textStyle}>{this.props.text}</div>}
			</div>
		);
	}

}
