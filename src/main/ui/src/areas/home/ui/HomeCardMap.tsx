import { Card, Icon } from "@salesforce/design-system-react";
import { API, Config } from "@zeitwert/ui-model";
import { AppCtx } from "app/App";
import BuildingMap, { BuildingInfo } from "areas/building/ui/components/BuildingMap";
import { makeObservable, observable, toJS } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

export interface HomeCardMapProps {
	onClick?: (itemId: string) => void;
}

@inject("appStore", "session")
@observer
export default class HomeCardMap extends React.Component<HomeCardMapProps> {

	@observable buildingCount: number = 0;
	@observable buildingList: BuildingInfo[] = [];

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
					!this.buildingList.length && !!this.buildingCount &&
					<p className="slds-m-horizontal_medium">Keine Koordinaten berechnet (aus {this.buildingCount} Gebäuden).</p>
				}
				{
					!this.buildingList.length && !this.buildingCount &&
					<p className="slds-m-horizontal_medium">Keine Immobilien vorhanden.</p>
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
		this.buildingCount = rsp.data.data.length;
		this.buildingList = rsp.data.data.map((b: any) => this.toBuilding(b)).filter((b: BuildingInfo) => !!b);
	}

	private toBuilding(json: any): BuildingInfo | undefined {
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
