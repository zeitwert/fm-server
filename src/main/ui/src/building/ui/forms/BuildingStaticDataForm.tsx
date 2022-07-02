
import { Card } from "@salesforce/design-system-react";
import { EnumeratedField, FieldGroup, FieldRow, Input, IntField, NumberField, Select, TextArea, TextField } from "@zeitwert/ui-forms";
import { BuildingModel, BuildingStore, Config, Enumerated } from "@zeitwert/ui-model";
import axios from "axios";
import { observer } from "mobx-react";
import { converters, Field, Form, Query } from "mstform";
import React from "react";

const loadBuildingSubTypes = async (q: Query): Promise<Enumerated[]> => {
	if (q.buildingType?.id) {
		const subTypesResponse = await axios.get(Config.getEnumUrl("building", "codeBuildingSubType/" + q.buildingType.id));
		if (subTypesResponse) {
			return subTypesResponse.data;
		}
	}
	return [];
};

const BuildingStaticDataFormModel = new Form(
	BuildingModel,
	{
		id: new Field(converters.string),
		name: new TextField({ required: true }),
		description: new TextField(),
		//
		buildingNr: new TextField({ required: true }),
		insuranceNr: new TextField(),
		plotNr: new TextField(),
		nationalBuildingId: new TextField(),
		historicPreservation: new EnumeratedField({ source: "{{enumBaseUrl}}/building/codeHistoricPreservation" }),
		//
		street: new TextField(),
		zip: new TextField(),
		city: new TextField(),
		country: new EnumeratedField({ source: "{{enumBaseUrl}}/account/codeCountry" }),
		//
		buildingYear: new IntField({ minValue: 1000, maxLength: 4 }),
		volume: new NumberField(),
		areaGross: new NumberField(),
		areaNet: new NumberField(),
		nrOfFloorsAboveGround: new NumberField(),
		nrOfFloorsBelowGround: new NumberField(),
		//
		currency: new EnumeratedField({ source: "{{enumBaseUrl}}/account/codeCurrency" }),
		insuredValue: new NumberField({ required: true }),
		insuredValueYear: new IntField({ required: true, minValue: 1800, maxLength: 4 }),
		notInsuredValue: new NumberField(),
		notInsuredValueYear: new IntField({ minValue: 1800, maxLength: 4 }),
		thirdPartyValue: new NumberField(),
		thirdPartyValueYear: new IntField({ minValue: 1800, maxLength: 4 }),
		//
		buildingType: new EnumeratedField({ source: "{{enumBaseUrl}}/building/codeBuildingType" }),
		buildingSubType: new EnumeratedField({
			source: loadBuildingSubTypes,
			dependentQuery: (accessor) => {
				return { buildingType: accessor.node.buildingType };
			}
		}),
	}
);

export interface BuildingStaticDataFormProps {
	store: BuildingStore;
}

@observer
export default class BuildingStaticDataForm extends React.Component<BuildingStaticDataFormProps> {

	formState: typeof BuildingStaticDataFormModel.FormStateType;

	constructor(props: BuildingStaticDataFormProps) {
		super(props);
		const building = props.store.item!;
		this.formState = BuildingStaticDataFormModel.state(
			building,
			{
				converterOptions: {
					decimalSeparator: ".",
					thousandSeparator: "'",
					renderThousands: true,
				},
				isReadOnly: (accessor) => {
					if (!props.store.isInTrx) {
						return true;
					}
					return false;
				},
				isDisabled: (accessor) => {
					if (["currency", "country"].indexOf(accessor.fieldref) >= 0) {
						return true;
					} else if (accessor.fieldref === "buildingSubType" && !building.buildingType) {
						return true;
					} else if (accessor.fieldref === "notInsuredValueYear") {
						return !building.notInsuredValue;
					} else if (accessor.fieldref === "thirdPartyValueYear") {
						return !building.thirdPartyValue;
					}
					return false;
				},
				isRequired: (accessor) => {
					if (accessor.fieldref === "notInsuredValueYear") {
						return !!building.notInsuredValue;
					} else if (accessor.fieldref === "thirdPartyValueYear") {
						return !!building.thirdPartyValue;
					}
					return false;
				},
			}
		);
		this.formState.field("buildingSubType").references.autoLoadReaction();
	}

	componentWillUnmount() {
		this.formState.field("buildingSubType").references.clearAutoLoadReaction();
	}

	render() {
		return (
			<div>
				<div className="slds-grid slds-wrap slds-m-top_small">
					<div className="slds-col slds-size_1-of-1 slds-large-size_1-of-2 slds-x-large-size_1-of-3">
						<Card heading="Grunddaten" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup>
										<FieldRow>
											<Input label="Nr" accessor={this.formState.field("buildingNr")} size={3} />
											<Input label="Name" type="text" accessor={this.formState.field("name")} size={9} />
										</FieldRow>
									</FieldGroup>
								</div>
							</div>
						</Card>
						<Card heading="Addresse" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup isAddress>
										<FieldRow>
											<Input label="Strasse" accessor={this.formState.field("street")} />
										</FieldRow>
										<FieldRow>
											<Input label="PLZ" accessor={this.formState.field("zip")} size={3} />
											<Input label="Ort" accessor={this.formState.field("city")} size={9} />
										</FieldRow>
										<FieldRow>
											<Select label="Land" accessor={this.formState.field("country")} />
										</FieldRow>
									</FieldGroup>
								</div>
							</div>
						</Card>
						<Card heading="Identifikation" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup>
										<FieldRow>
											<Input label="Police Gebäudeversicherung" accessor={this.formState.field("insuranceNr")} />
										</FieldRow>
										<FieldRow>
											<Input label="EGID" accessor={this.formState.field("nationalBuildingId")} />
										</FieldRow>
										<FieldRow>
											<Input label="Parzellen-Nr" accessor={this.formState.field("plotNr")} />
										</FieldRow>
									</FieldGroup>
								</div>
							</div>
						</Card>
					</div>
					<div className="slds-col slds-size_1-of-1 slds-large-size_1-of-2 slds-x-large-size_1-of-3">
						<Card heading="Bewertung" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<FieldGroup>
									<FieldRow>
										<Select label="Währung" accessor={this.formState.field("currency")} size={8} />
										<Input label="Baujahr" accessor={this.formState.field("buildingYear")} size={4} />
									</FieldRow>
									<FieldRow>
										<Input label="Versicherungswert (kCHF)" accessor={this.formState.field("insuredValue")} size={8} />
										<Input label="Jahr" accessor={this.formState.field("insuredValueYear")} size={4} />
									</FieldRow>
									<FieldRow>
										<Input label="Nicht versicherter Wert (kCHF)" accessor={this.formState.field("notInsuredValue")} size={8} />
										<Input label="Jahr" accessor={this.formState.field("notInsuredValueYear")} size={4} />
									</FieldRow>
									<FieldRow>
										<Input label="Wert Fremdeigentum (kCHF)" accessor={this.formState.field("thirdPartyValue")} size={8} />
										<Input label="Jahr" accessor={this.formState.field("thirdPartyValueYear")} size={4} />
									</FieldRow>
								</FieldGroup>
							</div>
						</Card>
						<Card heading="Dimensionen" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<FieldGroup>
									<FieldRow>
										<Input label="Volumen RI (m³)" accessor={this.formState.field("volume")} size={6} />
										<Input label="Fläche GF (m²)" accessor={this.formState.field("areaGross")} size={6} />
									</FieldRow>
									<FieldRow>
										<Input label="Geschosse oberirdisch" accessor={this.formState.field("nrOfFloorsAboveGround")} size={6} />
										<Input label="Geschosse unterirdisch" accessor={this.formState.field("nrOfFloorsBelowGround")} size={6} />
									</FieldRow>
								</FieldGroup>
							</div>
						</Card>
					</div>
					<div className="slds-col slds-size_1-of-1 slds-large-size_1-of-2 slds-x-large-size_1-of-3">
						<Card heading="Beschreibung" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<FieldGroup>
									<FieldRow>
										<Select label="Bauwerksart SIA I" accessor={this.formState.field("buildingType")} />
									</FieldRow>
									<FieldRow>
										<Select label="Bauwerksart SIA II" accessor={this.formState.field("buildingSubType")} />
									</FieldRow>
									<FieldRow>
										<Select label="Denkmalschutz" accessor={this.formState.field("historicPreservation")} />
									</FieldRow>
									<FieldRow>
										<TextArea label="Beschreibung" accessor={this.formState.field("description")} rows={12} />
									</FieldRow>
								</FieldGroup>
							</div>
						</Card>
					</div>
				</div >
			</div >
		);
	}

}
