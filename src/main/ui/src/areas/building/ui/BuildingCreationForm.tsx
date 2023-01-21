
import { Card } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow, Input, Select, SldsForm } from "@zeitwert/ui-forms";
import { asEnumerated, Building, BuildingModelType, Enumerated, session } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import { FormStateOptions } from "mstform";
import React from "react";
import BuildingForm from "./forms/BuildingForm";

export interface BuildingCreationFormProps {
	building: Building;
}

@observer
export default class BuildingCreationForm extends React.Component<BuildingCreationFormProps> {

	formStateOptions: FormStateOptions<BuildingModelType> = {
		isReadOnly: (accessor) => {
			if (!this.props.building.account) {
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
		const building = this.props.building;
		return (
			<SldsForm
				formModel={BuildingForm}
				formStateOptions={this.formStateOptions}
				item={this.props.building}
			>
				<Grid className="slds-wrap slds-m-top_small" isVertical={false}>
					<Col cols={1} totalCols={1}>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Inhaber">
								<FieldRow>
									<Select
										label="Kunde"
										required={true}
										value={asEnumerated(building.account)}
										values={this.accounts}
										onChange={(e) => building.setAccount(e!.id)}
										disabled={!!building.account?.id}
									/>
								</FieldRow>
							</FieldGroup>
						</Card>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Grunddaten">
								<FieldRow>
									<Input label="Gebäudenummer" fieldName="buildingNr" size={3} />
									<Input label="Name" type="text" fieldName="name" size={9} />
								</FieldRow>
								<FieldRow>
									<Select label="Verantwortlich" fieldName="owner" />
								</FieldRow>
							</FieldGroup>
						</Card>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Bewertung">
								<FieldRow>
									<Input label="Versicherungswert (kCHF)" fieldName="insuredValue" size={8} />
									<Input label="Jahr" fieldName="insuredValueYear" size={4} />
								</FieldRow>
							</FieldGroup>
						</Card>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Address" isAddress>
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
						</Card>
					</Col>
				</Grid>
			</SldsForm>
		);
	}

}
