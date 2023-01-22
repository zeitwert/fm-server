
import { Card } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow, Input, Select, SldsForm, TextArea } from "@zeitwert/ui-forms";
import { Building, BuildingModelType, CONTACT_API, Enumerated } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { computed, makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import { FormStateOptions } from "mstform";
import React from "react";
import BuildingForm from "../forms/BuildingForm";

export interface BuildingMainFormProps {
	building: Building;
	doEdit: boolean;
}

@observer
export default class BuildingMainForm extends React.Component<BuildingMainFormProps> {

	formStateOptions: FormStateOptions<BuildingModelType> = {
		isReadOnly: (accessor) => {
			if (!this.props.doEdit) {
				return true;
			}
			return false;
		},
		isDisabled: (accessor) => {
			const building = this.props.building;
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
			const building = this.props.building;
			if (accessor.fieldref === "notInsuredValueYear") {
				return !!building.notInsuredValue;
			} else if (accessor.fieldref === "thirdPartyValueYear") {
				return !!building.thirdPartyValue;
			}
			return false;
		},
	};

	@observable
	allContacts: Enumerated[] = [];

	@computed
	get availableContacts(): Enumerated[] {
		const building = this.props.building;
		return this.allContacts
			.filter(act => !building.contacts.find(bct => bct.id === act.id))
			.sort((a, b) => a.name < b.name ? -1 : 1);
	}

	constructor(props: BuildingMainFormProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		const allContacts = await CONTACT_API.getAggregates();
		this.allContacts = Object.values(allContacts.contact).map(ct => {
			return {
				id: ct.id,
				name: ct.caption,
				itemType: ct.meta.itemType
			}
		});
	}

	render() {
		const building = this.props.building;
		return (
			<SldsForm
				formModel={BuildingForm}
				formStateOptions={this.formStateOptions}
				item={this.props.building}
			>
				<Grid className="slds-wrap slds-m-top_small" isVertical={false}>
					<Col cols={1} totalCols={1} totalColsLarge={2} className="slds-x-large-size_1-of-3">
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Grunddaten">
								<FieldRow>
									<Input label="Nr" fieldName="buildingNr" size={3} />
									<Input label="Name" type="text" fieldName="name" size={9} />
								</FieldRow>
								<FieldRow>
									<Select label="Verantwortlich" fieldName="owner" />
								</FieldRow>
							</FieldGroup>
							<FieldGroup legend="Addresse" isAddress className="slds-m-top_medium">
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
						</Card>
					</Col>
					<Col cols={1} totalCols={1} totalColsLarge={2} className="slds-x-large-size_1-of-3">
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Identifikation" className="slds-m-top_medium">
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
							<FieldGroup legend="Klassifizierung">
								<FieldRow>
									<Select label="Bauwerksart SIA I" fieldName="buildingType" />
								</FieldRow>
								<FieldRow>
									<Select label="Bauwerksart SIA II" fieldName="buildingSubType" />
								</FieldRow>
								<FieldRow>
									<Select label="Denkmalschutz" fieldName="historicPreservation" />
								</FieldRow>
							</FieldGroup>
						</Card>
					</Col>
					<Col cols={1} totalCols={1} totalColsLarge={2} className="slds-x-large-size_1-of-3">
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Bewertung">
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
							<FieldGroup legend="Dimensionen" className="slds-m-top_medium">
								<FieldRow>
									<Input label="Volumen RI (m³)" fieldName="volume" size={6} />
									<Input label="Fläche GF (m²)" fieldName="areaGross" size={6} />
								</FieldRow>
								<FieldRow>
									<Input label="Geschosse oberirdisch" fieldName="nrOfFloorsAboveGround" size={6} />
									<Input label="Geschosse unterirdisch" fieldName="nrOfFloorsBelowGround" size={6} />
								</FieldRow>
							</FieldGroup>
						</Card>
					</Col>
				</Grid>
				<Grid className="slds-wrap slds-m-top_small" isVertical={false}>
					<Col cols={2} totalCols={3}>
						<Col cols={2} totalCols={2}>
							<Card heading={`Kontakte (${building.contacts.length})`} bodyClassName="slds-m-around_medium">
								<div className="slds-card__body xslds-card__body_inner" style={{ maxHeight: "300px", overflowY: "auto" }}>
									<table className="slds-table slds-table_cell-buffer slds-table_bordered">
										<thead>
											<tr className="slds-line-height_reset">
												<th className="" scope="col" style={{ width: "35%" }}>
													<div className="slds-truncate" title="Element">Name</div>
												</th>
												<th className="" scope="col" style={{ width: "20%" }}>
													<div className="slds-truncate" title="Typ">Rolle</div>
												</th>
												<th className="" scope="col" style={{ width: "20%" }}>
													<div className="slds-truncate" title="Typ">Mobile</div>
												</th>
												<th className="" scope="col" style={{ width: "25%" }}>
													<div className="slds-truncate" title="Typ">Email</div>
												</th>
												<th className="" scope="col" style={{ width: "5%" }}>
													<div className="slds-truncate" title="Aktion">Aktion</div>
												</th>
											</tr>
										</thead>
										<tbody>
											{
												building.contacts.map(ct => (
													<tr className="slds-hint-parent" key={"include-" + ct.id}>
														<th data-label="Kontakt" scope="row">
															<div className="slds-truncate">
																<a href={`/contact/${ct.id}`} tabIndex={-1}>{ct.caption}</a>
															</div>
														</th>
														<td data-label="Rolle">
															<div className="slds-truncate">{ct.contactRole?.name}</div>
														</td>
														<td data-label="Mobile">
															<div className="slds-truncate">{ct.mobile}</div>
														</td>
														<td data-label="Email">
															<div className="slds-truncate">{ct.email}</div>
														</td>
														<td data-label="Aktion">
															{
																this.props.doEdit &&
																<button className="slds-button slds-button_icon slds-button_icon-error" title="Entfernen" onClick={() => { building.removeContact(ct.id) }}>
																	<svg className="slds-button__icon" aria-hidden="true">
																		<use xlinkHref="/assets/icons/utility-sprite/svg/symbols.svg#close"></use>
																	</svg>
																</button>
															}
														</td>
													</tr>
												))
											}
										</tbody>
									</table>
								</div>
							</Card>
						</Col>
						<Col cols={1} totalCols={2}>
							{
								this.props.doEdit &&
								<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
									<FieldGroup legend="Kontakt hinzufügen">
										<FieldRow>
											<Select
												value={undefined}
												values={this.availableContacts}
												onChange={(e) => { e?.id && building.addContact(e.id) }}
											/>
										</FieldRow>
									</FieldGroup>
								</Card>
							}
						</Col>
					</Col>
					<Col cols={1} totalCols={3}>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Beschreibung">
								<FieldRow>
									<TextArea fieldName="description" rows={12} />
								</FieldRow>
							</FieldGroup>
						</Card>
					</Col>
				</Grid>
			</SldsForm>
		);
	}

}
