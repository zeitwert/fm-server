import { Card, Icon } from "@salesforce/design-system-react";
import { API, Config } from "@zeitwert/ui-model";
import { AppCtx } from "App";
import BuildingMap, { Building } from "building/ui/forms/BuildingMap";
import { makeObservable, observable, toJS } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

export interface HomeCardMapProps {
	onClick?: (itemId: string) => void;
}

@inject("appStore", "session")
@observer
export default class HomeCardMap extends React.Component<HomeCardMapProps> {

	@observable buildingList: Building[] = [];

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: any) {
		super(props);
		makeObservable(this);
	}

	componentDidMount() {
		this.loadBuildingList();
	}

	render() {
		return (
			<Card
				icon={<Icon category="standard" name="location" size="small" />}
				heading={<b>{"Übersicht Bestand"}</b>}
				className="fa-height-100"
				bodyClassName="slds-m-around_none"
			>
				{
					!this.buildingList.length &&
					<p className="slds-m-horizontal_medium">Keine Immobilien vorhanden oder keine Koordinaten berechnet.</p>
				}
				{
					!!this.buildingList.length &&
					<BuildingMap buildings={toJS(this.buildingList)} onClick={(building) => this.props.onClick?.(building.id)} />
				}
			</Card>
		);
	}

	private loadBuildingList = async () => {
		const rsp = await API.get(Config.getApiUrl("building", "buildings"))
		this.buildingList = rsp.data.data.map((b: any) => this.toBuilding(b)).filter((b: Building) => !!b);
	}

	private toBuilding(json: any): Building | undefined {
		if (json.attributes.geoCoordinates?.startsWith("WGS:")) {
			const coords = json.attributes.geoCoordinates.substring(4).split(",");
			const lat = parseFloat(coords?.[0]!);
			const lng = parseFloat(coords?.[1]!);
			return {
				id: json.id,
				name: json.attributes.name,
				address: `${json.attributes.street}, ${json.attributes.zip} ${json.attributes.city}`,
				lat: lat,
				lng: lng
			};
		}
		return undefined;
	}

}
