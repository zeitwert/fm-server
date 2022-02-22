
import Card from "@salesforce/design-system-react/components/card";
import { EnumeratedField, FieldGroup, FieldRow, Input, Select, TextField } from "@zeitwert/ui-forms";
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
		//
		buildingNr: new TextField({ required: true }),
		buildingInsuranceNr: new TextField(),
		plotNr: new TextField(),
		nationalBuildingId: new TextField(),
		//
		street: new TextField({ required: true }),
		zip: new TextField({ required: true }),
		city: new TextField({ required: true }),
		country: new EnumeratedField({ required: true, source: "{{enumBaseUrl}}/common/codeCountry" }),
	}
);

export interface BuildingCreationFormProps {
	store: BuildingStore;
}

@observer
export default class BuildingCreationForm extends React.Component<BuildingCreationFormProps> {

	formState: typeof BuildingCreationFormModel.FormStateType;

	@observable
	communities: Enumerated[] = [];

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
			this.communities = userInfoResponse.communities;
		}
	}

	render() {
		const building = this.props.store.item! as Building;
		return (
			<div>
				<div className="slds-grid slds-wrap slds-m-top_small">
					<div className="slds-col slds-size_1-of-1">
						<Card heading="Gemeinde" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup>
										<FieldRow>
											<Select
												label="Gemeinde"
												value={building.account?.id}
												values={this.communities}
												onChange={(e) => this.props.store.item!.setAccount(e.target.value?.toString())}
											/>
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
											<Input label="Gebäudenummer" accessor={this.formState.field("buildingNr")} />
										</FieldRow>
										<FieldRow>
											<Input label="Name" type="text" accessor={this.formState.field("name")} />
										</FieldRow>
										<FieldRow>
											<Input label="Nr Police GV" accessor={this.formState.field("buildingInsuranceNr")} />
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
