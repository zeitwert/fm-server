
import { Card } from "@salesforce/design-system-react";
import { EnumeratedField, FieldGroup, FieldRow, Input, NumberField, Select, TextField } from "@zeitwert/ui-forms";
import { BuildingModel, BuildingStore } from "@zeitwert/ui-model";
import { observer } from "mobx-react";
import { Form } from "mstform";
import React from "react";
import BuildingMap from "./BuildingMap";

const ALT_GEO_ADDRESS_HELP_TEXT = "<b>Alternative Geo Addresse</b><ul class=\"slds-list_dotted\"><li>Adresse (Strasse Nr, PLZ Ort)</li><li>Plus Code (z.B. 9HG5+8P Zürich)</li><li>Koordinaten (z.B. 47.36489,8.676913)</li></ul>";

const BuildingLocationFormModel = new Form(
	BuildingModel,
	{
		street: new TextField(),
		zip: new TextField(),
		city: new TextField(),
		country: new EnumeratedField({ source: "{{enumBaseUrl}}/common/codeCountry" }),
		//
		geoAddress: new TextField(),
		geoCoordinates: new TextField(),
		geoZoom: new NumberField(),
		//
		nationalBuildingId: new TextField(),
		plotNr: new TextField(),
	}
);

export interface BuildingLocationFormProps {
	store: BuildingStore;
}

@observer
export default class BuildingLocationForm extends React.Component<BuildingLocationFormProps> {

	formState: typeof BuildingLocationFormModel.FormStateType;

	constructor(props: BuildingLocationFormProps) {
		super(props);
		const building = props.store.item!;
		this.formState = BuildingLocationFormModel.state(
			building,
			{
				converterOptions: {
					decimalSeparator: ".",
					thousandSeparator: "'",
					renderThousands: true,
				},
				isReadOnly: (accessor) => {
					if (["geoCoordinates", "geoZoom"].indexOf(accessor.fieldref) >= 0) {
						return true;
					} else if (!props.store.isInTrx) {
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
				isRequired: (accessor) => {
					return false;
				},
			}
		);
	}

	render() {
		const building = this.props.store.building!;
		let lat: number | undefined;
		let lng: number | undefined;
		if (building.geoCoordinates?.startsWith("WGS:")) {
			const coords = building.geoCoordinates.substring(4).split(",");
			lat = parseFloat(coords?.[0]!);
			lng = parseFloat(coords?.[1]!);
		}
		return (
			<div>
				<div className="slds-grid slds-wrap slds-m-top_small">
					<div className="slds-col slds-size_1-of-3">
						<Card heading="Addresse" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup isAddress>
										<FieldRow>
											<Input label="Strasse" accessor={this.formState.field("street")} />
										</FieldRow>
										<FieldRow>
											<Input label="PLZ" accessor={this.formState.field("zip")} size={4} />
											<Input label="Ort" accessor={this.formState.field("city")} size={8} />
										</FieldRow>
										<FieldRow>
											<Select label="Land" accessor={this.formState.field("country")} />
										</FieldRow>
										<FieldRow>
											<Input label="Alternative Geo Addresse" accessor={this.formState.field("geoAddress")} helpText={ALT_GEO_ADDRESS_HELP_TEXT} />
										</FieldRow>
									</FieldGroup>
								</div>
							</div>
						</Card>
						<Card heading="Lage" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup>
										<FieldRow>
											<Input label="Koordinaten" accessor={this.formState.field("geoCoordinates")} size={8} />
											<Input label="Zoom" accessor={this.formState.field("geoZoom")} size={4} />
										</FieldRow>
									</FieldGroup>
								</div>
							</div>
						</Card>
					</div>
					<div className="slds-col slds-size_2-of-3" key={"d-" + building.geoZoom + "-" + this.props.store.isInTrx}>
						<div style={{ height: "50vh", width: "98%" }}>
							{!!lat && !!lng && <BuildingMap name={building.name!} lat={lat} lng={lng} zoom={building.geoZoom!} onZoomChange={this.onZoomChange} />}
							{(!lat || !lng) && <div>Bitte Koordinaten auflösen (allenfalls alternative Geo Adresse erfassen)!</div>}
						</div>
					</div>
				</div >
			</div >
		);
	}

	private onZoomChange = (zoom: number): void => {
		if (this.props.store.isInTrx) {
			this.props.store.building!.setField("geoZoom", zoom);
		}
	}

}
