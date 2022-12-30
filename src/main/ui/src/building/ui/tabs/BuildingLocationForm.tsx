
import { Card } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow, Input, Select, SldsForm } from "@zeitwert/ui-forms";
import { BuildingModel, BuildingModelType, BuildingStore } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { observer } from "mobx-react";
import { Form, FormStateOptions } from "mstform";
import React from "react";
import BuildingMap, { Building } from "../components/BuildingMap";
import BuildingFormDef from "../forms/BuildingFormDef";

const ALT_GEO_ADDRESS_HELP_TEXT = "<b>Alternative Geo Addresse</b><ul class=\"slds-list_dotted\"><li>Adresse (Strasse Nr, PLZ Ort)</li><li>Plus Code (z.B. 9HG5+8P Zürich)</li><li>Koordinaten (z.B. 47.36489,8.676913)</li></ul>";

export interface BuildingLocationFormProps {
	store: BuildingStore;
}

const BuildingForm = new Form(
	BuildingModel,
	BuildingFormDef
);

@observer
export default class BuildingLocationForm extends React.Component<BuildingLocationFormProps> {

	formStateOptions: FormStateOptions<BuildingModelType> = {
		isReadOnly: (accessor) => {
			if (["geoCoordinates", "geoZoom"].indexOf(accessor.fieldref) >= 0) {
				return true;
			} else if (!this.props.store.isInTrx) {
				return true;
			}
			return false;
		},
		isDisabled: (accessor) => {
			if (["country"].indexOf(accessor.fieldref) >= 0) {
				return true;
			}
			return false;
		},
	};

	render() {
		const building = this.props.store.building!;
		let lat: number | undefined;
		let lng: number | undefined;
		let buildingInfo: Building | undefined;
		if (building.geoCoordinates?.startsWith("WGS:")) {
			const coords = building.geoCoordinates.substring(4).split(",");
			lat = parseFloat(coords?.[0]!);
			lng = parseFloat(coords?.[1]!);
			buildingInfo = {
				id: building.id,
				name: building.name!,
				address: `${building.street}, ${building.zip} ${building.city}`,
				lat: lat,
				lng: lng
			};
		}
		return (
			<SldsForm formModel={BuildingForm} formStateOptions={this.formStateOptions} item={this.props.store.building!}>
				<Grid className="slds-wrap slds-m-top_small" isVertical={false}>
					<Col cols={1} totalCols={3}>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup isAddress legend="Addresse">
								<FieldRow>
									<Input label="Strasse" fieldName="street" />
								</FieldRow>
								<FieldRow>
									<Input label="PLZ" fieldName="zip" size={4} />
									<Input label="Ort" fieldName="city" size={8} />
								</FieldRow>
								<FieldRow>
									<Select label="Land" fieldName="country" />
								</FieldRow>
							</FieldGroup>
							<FieldGroup legend="Alternative Geo-Addresse" className="slds-m-top_medium">
								<FieldRow>
									<Input label="Addresse" fieldName="geoAddress" helpText={ALT_GEO_ADDRESS_HELP_TEXT} />
								</FieldRow>
							</FieldGroup>
							<FieldGroup legend="Aufgelöste Geo-Addresse" className="slds-m-top_medium">
								<FieldRow>
									<Input label="Koordinaten" fieldName="geoCoordinates" size={8} />
									<Input label="Zoom" fieldName="geoZoom" size={4} />
								</FieldRow>
							</FieldGroup>
						</Card>
					</Col>
					<Col cols={2} totalCols={3}>
						<div style={{ height: "50vh", width: "98%" }}>
							{buildingInfo && <BuildingMap buildings={[buildingInfo]} zoom={building.geoZoom!} onZoomChange={this.onZoomChange} />}
							{!buildingInfo && <div>Bitte Koordinaten auflösen (allenfalls alternative Geo Adresse erfassen)!</div>}
						</div>
					</Col>
				</Grid >
			</SldsForm >
		);
	}

	private onZoomChange = (zoom: number): void => {
		if (this.props.store.isInTrx) {
			this.props.store.building!.setField("geoZoom", zoom);
		}
	}

}
