
import GoogleMapReact from "google-map-react";
import { observer } from "mobx-react";
import React from "react";

const GOOGLE_API_KEY = "AIzaSyBQF6Fi_Z0tZxVh5Eqzfx2m7hK3n718jsI";

interface BuildingMapProps {
	name: string;
	lat: number;
	lng: number;
	zoom: number;
	onZoomChange: (zoom: number) => void;
}

@observer
export default class BuildingMap extends React.Component<BuildingMapProps> {

	render() {
		const { name, lat, lng, zoom } = this.props;
		return (
			<GoogleMapReact
				bootstrapURLKeys={{ key: GOOGLE_API_KEY }}
				defaultCenter={{ lat: lat, lng: lng }}
				defaultZoom={zoom}
				onZoomAnimationStart={this.onZoom}
			>
				<SimpleMarker name={name} lat={lat} lng={lng} />
			</GoogleMapReact>
		);
	}

	private onZoom = (zoom: number): void => {
		this.props.onZoomChange?.(zoom);
	}

};

interface BuildingMarkerProps {
	name: string;
	lat: number;
	lng: number;
}

// const BuildingMarker = ({ name }: BuildingMarkerProps) => (<div>{name}</div>);

const SimpleMarker = (props: BuildingMarkerProps) => (
	<div
		className="simpleMarker"
		style={{
			transform: `translate3D(0,0,0) scale(0.6, 0.6)`,
		}}
	>
	</div>
);
