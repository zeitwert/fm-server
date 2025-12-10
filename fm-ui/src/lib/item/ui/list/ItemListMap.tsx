
import { observer } from "mobx-react";
import React, { FC } from "react";

// const IMG_BASE_URL = "/assets/img/";
// const PLACE = "villa.png";
// const PLACE_SEL = "villa-select.png";

// const ICONS: Record<string, { icon: string }> = {
// 	place: {
// 		icon: IMG_BASE_URL + PLACE,
// 	},
// 	place_sel: {
// 		icon: IMG_BASE_URL + PLACE_SEL,
// 	},
// };

export interface BuildingInfo {
	id: string;
	link: string;
	name: string;
	address: string;
	buildingType: string;
	accountId: string;
	accountLink: string;
	accountName: string;
	hasPosition: boolean;
	//position?: google.maps.LatLngLiteral;
}

interface ItemListMapProps {
	buildings: BuildingInfo[];
	currentBuildingId: string | undefined;
	onMouseEnter: (id: string) => void;
	onMouseLeave: (id: string) => void;
}

const ItemListMap: FC<ItemListMapProps> = observer((props) => {

	// const { buildings, currentBuildingId } = props;
	//	let markers = React.useRef<{ [key: string]: google.maps.Marker }>({});
	//	let [map, setMap] = React.useState<google.maps.Map>();
	// let [activeBuildingId, setActiveBuildingId] = React.useState<string>();
	const ref = React.useRef<HTMLDivElement>(null);

	// React.useEffect(() => {
	// 	if (ref.current && !map) {
	// 		setMap(new window.google.maps.Map(ref.current));
	// 	}
	// }, [ref, map]);

	// React.useEffect(() => {
	// 	if (!!map && buildings.length) {
	// 		let bounds = new google.maps.LatLngBounds();
	// 		buildings.filter(b => b.hasPosition).forEach(building => {
	// 			const position = new google.maps.LatLng(building.position?.lat!, building.position?.lng!);
	// 			bounds.extend(position)
	// 			markers.current[building.id] = createMarker(props, building, map!, position);
	// 		});
	// 		map.fitBounds(bounds);
	// 	}
	// }, [props, map, buildings]);

	// React.useEffect(() => {
	// 	if (!!activeBuildingId !== !!currentBuildingId) {
	// 		if (!!activeBuildingId && markers.current[activeBuildingId]) {
	// 			markers.current[activeBuildingId].setIcon(ICONS.place.icon);
	// 		} else if (!!currentBuildingId && markers.current[currentBuildingId]) {
	// 			markers.current[currentBuildingId].setIcon(ICONS.place_sel.icon);
	// 		}
	// 		setActiveBuildingId(currentBuildingId);
	// 	}
	// }, [activeBuildingId, currentBuildingId]);

	return (
		<div className="slds-map_container">
			<div className="slds-map" tabIndex={0}>
				<div ref={ref} id="map" />
			</div>
		</div>
	);

});

// const eventListeners: { [key: string]: google.maps.MapsEventListener } = {};

// function createMarker(props: ItemListMapProps, building: BuildingInfo, map: google.maps.Map, position: google.maps.LatLng): google.maps.Marker {
// 	const marker = new google.maps.Marker({
// 		title: building.name + "\n" + building.address,
// 		position: position,
// 		icon: ICONS.place.icon,
// 		map: map,
// 		optimized: false
// 	});
// 	eventListeners[building.id] = google.maps.event.addListener(marker, 'mouseover', () => { handleMouseEnter(marker, building.id, props) });
// 	return marker;
// }

// function handleMouseEnter(marker: google.maps.Marker, id: string, props: ItemListMapProps): void {
// 	google.maps.event.removeListener(eventListeners[id]!);
// 	props.onMouseEnter(id);
// 	setTimeout(() => {
// 		eventListeners[id] = google.maps.event.addListener(marker, 'mouseout', () => { handleMouseLeave(marker, id, props) });
// 	}, 100);
// }

// function handleMouseLeave(marker: google.maps.Marker, id: string, props: ItemListMapProps): void {
// 	google.maps.event.removeListener(eventListeners[id]!);
// 	props.onMouseLeave(id);
// 	setTimeout(() => {
// 		eventListeners[id] = google.maps.event.addListener(marker, 'mouseover', () => { handleMouseEnter(marker, id, props) });
// 	}, 100);
// }

export default ItemListMap;
