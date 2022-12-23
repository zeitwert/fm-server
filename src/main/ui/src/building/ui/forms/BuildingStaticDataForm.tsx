
import { Card } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow, Input, Select, SldsForm, TextArea } from "@zeitwert/ui-forms";
import { BuildingStore } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { observer } from "mobx-react";
import { FormStateOptions } from "mstform";
import React from "react";
import BuildingFormModel from "./BuildingFormModel";

export interface BuildingStaticDataFormProps {
	store: BuildingStore;
}

@observer
export default class BuildingStaticDataForm extends React.Component<BuildingStaticDataFormProps> {

	FORM_OPTIONS: FormStateOptions<typeof BuildingFormModel> = {
		isReadOnly: (accessor) => {
			if (!this.props.store.isInTrx) {
				return true;
			}
			return false;
		},
		isDisabled: (accessor) => {
			if (["currency", "country"].indexOf(accessor.fieldref) >= 0) {
				return true;
			} else if (accessor.fieldref === "buildingSubType" && !this.props.store.building?.buildingType) {
				return true;
			} else if (accessor.fieldref === "notInsuredValueYear") {
				return !this.props.store.building?.notInsuredValue;
			} else if (accessor.fieldref === "thirdPartyValueYear") {
				return !this.props.store.building?.thirdPartyValue;
			}
			return false;
		},
		isRequired: (accessor) => {
			if (accessor.fieldref === "notInsuredValueYear") {
				return !!this.props.store.building?.notInsuredValue;
			} else if (accessor.fieldref === "thirdPartyValueYear") {
				return !!this.props.store.building?.thirdPartyValue;
			}
			return false;
		},
	};

	constructor(props: BuildingStaticDataFormProps) {
		super(props);
		this.formState.field("buildingSubType").references.autoLoadReaction();
	}

	componentWillUnmount() {
		this.formState.field("buildingSubType").references.clearAutoLoadReaction();
	}

	render() {
		return (
			<SldsForm formModel={BuildingFormModel} options={this.FORM_OPTIONS} item={this.props.store.building!}>
				<Grid className="slds-wrap slds-m-top_small" isVertical={false}>
					<Col cols={1} totalCols={1} totalColsLarge={2} className="slds-x-large-size_1-of-3">
						<Card heading="Grunddaten" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<FieldGroup>
									<FieldRow>
										<Input label="Nr" fieldName="buildingNr" size={3} />
										<Input label="Name" type="text" fieldName="name" size={9} />
									</FieldRow>
									<FieldRow>
										<Select label="Verantwortlich" fieldName="owner" />
									</FieldRow>
								</FieldGroup>
							</div>
						</Card>
						<Card heading="Addresse" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<FieldGroup isAddress>
									<FieldRow>
										<Input label="Strasse" fieldName="street" />
									</FieldRow>
									<FieldRow>
										<Input label="PLZ" fieldName="zip" size={3} />
										<Input label="Ort" fieldName="city" size={9} />
									</FieldRow>
									<FieldRow>
										<Select label="Land" fieldName="country" />
									</FieldRow>
								</FieldGroup>
							</div>
						</Card>
						<Card heading="Identifikation" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<FieldGroup>
									<FieldRow>
										<Input label="Police Gebäudeversicherung" fieldName="insuranceNr" />
									</FieldRow>
									<FieldRow>
										<Input label="EGID" fieldName="nationalBuildingId" />
									</FieldRow>
									<FieldRow>
										<Input label="Parzellen-Nr" fieldName="plotNr" />
									</FieldRow>
								</FieldGroup>
							</div>
						</Card>
					</Col>
					<Col cols={1} totalCols={1} totalColsLarge={2} className="slds-x-large-size_1-of-3">
						<Card heading="Bewertung" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<FieldGroup>
									<FieldRow>
										<Select label="Währung" fieldName="currency" size={8} />
										<Input label="Baujahr" fieldName="buildingYear" size={4} />
									</FieldRow>
									<FieldRow>
										<Input label="Versicherungswert (kCHF)" fieldName="insuredValue" size={8} />
										<Input label="Jahr" fieldName="insuredValueYear" size={4} />
									</FieldRow>
									<FieldRow>
										<Input label="Nicht versicherter Wert (kCHF)" fieldName="notInsuredValue" size={8} />
										<Input label="Jahr" fieldName="notInsuredValueYear" size={4} />
									</FieldRow>
									<FieldRow>
										<Input label="Wert Fremdeigentum (kCHF)" fieldName="thirdPartyValue" size={8} />
										<Input label="Jahr" fieldName="thirdPartyValueYear" size={4} />
									</FieldRow>
								</FieldGroup>
							</div>
						</Card>
						<Card heading="Dimensionen" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<FieldGroup>
									<FieldRow>
										<Input label="Volumen RI (m³)" fieldName="volume" size={6} />
										<Input label="Fläche GF (m²)" fieldName="areaGross" size={6} />
									</FieldRow>
									<FieldRow>
										<Input label="Geschosse oberirdisch" fieldName="nrOfFloorsAboveGround" size={6} />
										<Input label="Geschosse unterirdisch" fieldName="nrOfFloorsBelowGround" size={6} />
									</FieldRow>
								</FieldGroup>
							</div>
						</Card>
					</Col>
					<Col cols={1} totalCols={1} totalColsLarge={2} className="slds-x-large-size_1-of-3">
						<Card heading="Beschreibung" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<FieldGroup>
									<FieldRow>
										<Select label="Bauwerksart SIA I" fieldName="buildingType" />
									</FieldRow>
									<FieldRow>
										<Select label="Bauwerksart SIA II" fieldName="buildingSubType" />
									</FieldRow>
									<FieldRow>
										<Select label="Denkmalschutz" fieldName="historicPreservation" />
									</FieldRow>
									<FieldRow>
										<TextArea label="Beschreibung" fieldName="description" rows={12} />
									</FieldRow>
								</FieldGroup>
							</div>
						</Card>
					</Col>
				</Grid>
			</SldsForm>
		);
	}

}
