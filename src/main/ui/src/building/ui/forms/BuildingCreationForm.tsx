
import { Card } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow, Input, Select, SldsForm } from "@zeitwert/ui-forms";
import { Building, BuildingStore, Enumerated, session } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import { FormStateOptions } from "mstform";
import React from "react";
import BuildingFormModel from "./BuildingFormModel";

export interface BuildingCreationFormProps {
	store: BuildingStore;
}

@observer
export default class BuildingCreationForm extends React.Component<BuildingCreationFormProps> {

	FORM_OPTIONS: FormStateOptions<typeof BuildingFormModel> = {
		isReadOnly: (accessor) => {
			if (!this.props.store.isInTrx) {
				return true;
			} else if (!this.props.store.building?.account) {
				return true;
			}
			return false;
		},
		isDisabled: (accessor) => {
			if (["currency", "country"].indexOf(accessor.fieldref) >= 0) {
				return true;
			}
			return false;
		},
	};

	@observable
	accounts: Enumerated[] = [];

	constructor(props: BuildingCreationFormProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		const tenantInfo = await session.tenantInfo(session.sessionInfo!.tenant.id);
		if (tenantInfo) {
			this.accounts = tenantInfo.accounts;
		}
	}

	render() {
		const building = this.props.store.item! as Building;
		return (
			<SldsForm formModel={BuildingFormModel} options={this.FORM_OPTIONS} item={this.props.store.building!}>
				<Grid className="slds-wrap slds-m-top_small" isVertical={false}>
					<Col cols={1} totalCols={1}>
						<Card heading="Inhaber" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<FieldGroup>
									<FieldRow>
										<Select
											label="Kunde"
											required={true}
											value={building.account?.id}
											values={this.accounts}
											onChange={(e) => this.props.store.item!.setAccount(e.target.value?.toString())}
											disabled={!!building.account?.id}
										/>
									</FieldRow>
								</FieldGroup>
							</div>
						</Card>
						<Card heading="Grunddaten" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<FieldGroup>
									<FieldRow>
										<Input label="Gebäudenummer" fieldName="buildingNr" size={3} />
										<Input label="Name" type="text" fieldName="name" size={9} />
									</FieldRow>
									<FieldRow>
										<Select label="Verantwortlich" fieldName="owner" />
									</FieldRow>
								</FieldGroup>
							</div>
						</Card>
						<Card heading="Bewertung" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<FieldGroup>
									<FieldRow>
										<Input label="Versicherungswert (kCHF)" fieldName="insuredValue" size={8} />
										<Input label="Jahr" fieldName="insuredValueYear" size={4} />
									</FieldRow>
								</FieldGroup>
							</div>
						</Card>
						<Card heading="Addresse" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<FieldGroup /*label="Address"*/ isAddress>
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
							</div>
						</Card>
					</Col>
				</Grid>
			</SldsForm>
		);
	}

}
