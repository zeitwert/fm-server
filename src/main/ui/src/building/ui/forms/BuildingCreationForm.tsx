
import { Card } from "@salesforce/design-system-react";
import { EnumeratedField, FieldGroup, FieldRow, Input, IntField, NumberField, Select, TextField } from "@zeitwert/ui-forms";
import { Building, BuildingModel, BuildingStore, Enumerated, session } from "@zeitwert/ui-model";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import { converters, Field, Form } from "mstform";
import React from "react";

const BuildingCreationFormModel = new Form(
	BuildingModel,
	{
		id: new Field(converters.string),
		name: new TextField({ required: true }),
		buildingNr: new TextField({ required: true }),
		//
		insuredValue: new NumberField({ required: true }),
		insuredValueYear: new IntField({ required: true, minValue: 1000, maxLength: 4 }),
		//
		street: new TextField({ required: true }),
		zip: new TextField({ required: true }),
		city: new TextField({ required: true }),
		country: new EnumeratedField({ required: true, source: "{{enumBaseUrl}}/oe/codeCountry" }),
	}
);

export interface BuildingCreationFormProps {
	store: BuildingStore;
}

@observer
export default class BuildingCreationForm extends React.Component<BuildingCreationFormProps> {

	formState: typeof BuildingCreationFormModel.FormStateType;

	@observable
	accounts: Enumerated[] = [];

	constructor(props: BuildingCreationFormProps) {
		super(props);
		makeObservable(this);
		const building = props.store.item!;
		this.formState = BuildingCreationFormModel.state(
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
					} else if (!building.account) {
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
			}
		);
	}

	async componentDidMount() {
		const userInfoResponse = await session.userInfo(session.sessionInfo!.user.email);
		if (userInfoResponse) {
			this.accounts = userInfoResponse.accounts;
		}
	}

	render() {
		const building = this.props.store.item! as Building;
		return (
			<div>
				<div className="slds-grid slds-wrap slds-m-top_small">
					<div className="slds-col slds-size_1-of-1">
						<Card heading="Inhaber" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
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
							</div>
						</Card>
						<Card heading="Grunddaten" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup>
										<FieldRow>
											<Input label="Name" type="text" accessor={this.formState.field("name")} />
										</FieldRow>
										<FieldRow>
											<Input label="Gebäudenummer" accessor={this.formState.field("buildingNr")} />
										</FieldRow>
									</FieldGroup>
								</div>
							</div>
						</Card>
						<Card heading="Bewertung" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<FieldGroup>
									<FieldRow>
										<Input label="Versicherungswert (kCHF)" accessor={this.formState.field("insuredValue")} size={8} />
										<Input label="Jahr" accessor={this.formState.field("insuredValueYear")} size={4} />
									</FieldRow>
								</FieldGroup>
							</div>
						</Card>
						<Card heading="Addresse" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup /*label="Address"*/ isAddress>
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
									</FieldGroup>
								</div>
							</div>
						</Card>
					</div>
				</div>
			</div>
		);
	}

}
