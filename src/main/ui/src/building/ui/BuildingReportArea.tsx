
import { Form } from "@finadvise/forms";
import { Card } from "@salesforce/design-system-react";
import { API, Config, jsonApiFetch } from "@zeitwert/ui-model";
import { observable } from "mobx";
import { observer } from "mobx-react";
import TabProjection from "projection/ui/TabProjectionChart";
import React from "react";
import { EMPTY_RESULT, ProjectionResult } from "./ProjectionResult";

const API_BASE_URL = Config.getApiUrl("##", "##").replace("/##/##", "");
const ENUM_BASE_URL = Config.getEnumUrl("##", "##").replace("/##/##", "");

@observer
export default class BuildingReportArea extends React.Component {

	@observable
	projection: ProjectionResult = EMPTY_RESULT;

	@observable
	payload: any = {
		buildingPart: undefined,
		building: undefined,
		account: undefined
	};

	render() {
		return (
			<Card heading="Reporting" className="fa-height-100">
				<div className="slds-m-around_small">
					<Form
						config={FormDef}
						payload={this.payload}
						onAfterChange={this.onAfterChange}
						additionalData={Object.assign(
							{},
							{
								apiBaseUrl: API_BASE_URL,
								enumBaseUrl: ENUM_BASE_URL
							}
						)}
						fetch={jsonApiFetch}
					/>
					<TabProjection projection={this.projection} />
				</div>
			</Card>
		);
	}

	private onAfterChange = (path: string, value: any) => {
		this.payload = Object.assign(
			{},
			this.payload,
			{
				buildingPart: undefined,
				building: undefined,
				account: undefined
			},
			{ [path]: value }
		);
		if (!!this.payload.building) {
			this.loadBuildingSimulation(this.payload.building);
		} else if (!!this.payload.buildingPart) {
			this.loadPartSimulation(this.payload.buildingPart.id);
		}
	};

	private loadBuildingSimulation = async (buildingId: string) => {
		this.projection = await (await API.get(Config.getApiUrl("building", "projection/buildings/" + buildingId))).data;
		this.forceUpdate();
	};

	private loadPartSimulation = async (partId: string) => {
		this.projection = await (await API.get(Config.getApiUrl("building", "projection/elements/" + partId))).data;
		this.forceUpdate();
	};

}

const FormDef = {
	rows: [
		{
			cols: [
				{
					size: 4,
					type: "select",
					label: "Building",
					value: "{{building}}",
					dataSource: "Rest",
					storage: "Id",
					lookupUrl: "{{apiBaseUrl}}/buildling/buildlings/{{$id}}",
					identity: "id",
					formatItem: "{{$item.caption}}",
					queryUrl: "{{apiBaseUrl}}/building/buildings?filter[searchText]={{$searchText}}",
					enableAutocomplete: true,
					autocompleteMinLength: 2,
					path: ""
				},
				{
					size: 4,
					type: "select",
					label: "Account",
					value: "{{account}}",
					dataSource: "Rest",
					storage: "Id",
					lookupUrl: "{{apiBaseUrl}}/account/accounts/{{$id}}",
					identity: "id",
					formatItem: "{{$item.caption}}",
					queryUrl: "{{apiBaseUrl}}/account/accounts?filter[searchText]={{$searchText}}",
					enableAutocomplete: true,
					autocompleteMinLength: 2,
					path: ""
				},
				{
					size: 4,
					type: "select",
					label: "Part",
					value: "{{buildingPart}}",
					dataSource: "Rest",
					storage: "Full",
					identity: "id",
					enableAutocomplete: false,
					queryUrl: "{{enumBaseUrl}}/building/codeBuildingPart",
					path: "",
					formatItem: "{{$item.name}}"
				}
			]
		}
	]
};
