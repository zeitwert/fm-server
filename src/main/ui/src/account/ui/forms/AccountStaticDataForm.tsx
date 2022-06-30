
import { Card } from "@salesforce/design-system-react";
import { EnumeratedField, FieldGroup, FieldRow, Input, Select, Static, TextArea, TextField } from "@zeitwert/ui-forms";
import { AccountModel, AccountStore } from "@zeitwert/ui-model";
import { observer } from "mobx-react";
import { converters, Field, Form } from "mstform";
import React from "react";

// const loadContacts = async (q: Query): Promise<Enumerated[]> => {
// 	if (q.buildingType?.id) {
// 		const subTypesResponse = await axios.get(Config.getEnumUrl("building", "codeBuildingSubType/" + q.buildingType.id));
// 		if (subTypesResponse) {
// 			return subTypesResponse.data;
// 		}
// 	}
// 	return [];
// };

const AccountStaticDataFormModel = new Form(
	AccountModel,
	{
		id: new Field(converters.string),
		key: new TextField({ required: true }),
		name: new TextField({ required: true }),
		description: new TextField(),
		//
		accountType: new EnumeratedField({ source: "{{enumBaseUrl}}/account/codeAccountType", required: true }),
		clientSegment: new EnumeratedField({ source: "{{enumBaseUrl}}/account/codeClientSegment" }),
		//mainContact: new EnumeratedField({ source: loadContacts }),
	}
);

export interface AccountStaticDataFormProps {
	store: AccountStore;
}

@observer
export default class AccountStaticDataForm extends React.Component<AccountStaticDataFormProps> {

	formState: typeof AccountStaticDataFormModel.FormStateType;

	constructor(props: AccountStaticDataFormProps) {
		super(props);
		this.formState = AccountStaticDataFormModel.state(
			this.props.store.item!,
			{
				converterOptions: {
					decimalSeparator: ".",
					thousandSeparator: "'",
					renderThousands: true,
				},
				isReadOnly: (accessor) => {
					if (!this.props.store.isInTrx) {
						return true;
					} else if (["key"].indexOf(accessor.fieldref) >= 0) {
						return true;
					}
					return false;
				},
				isDisabled: (accessor) => {
					return false;
				},
				isRequired: (accessor) => {
					return false;
				},
			}
		);
		//this.formState.field("buildingSubType").references.autoLoadReaction();
	}

	componentWillUnmount() {
		//this.formState.field("buildingSubType").references.clearAutoLoadReaction();
	}

	render() {
		return (
			<div>
				<div className="slds-grid slds-wrap slds-m-top_small">
					<div className="slds-col slds-size_1-of-1">
						<Card heading="Information" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup>
										<FieldRow>
											<Input label="Name" type="text" accessor={this.formState.field("name")} size={4} />
											<Static label="Key" value={this.props.store.account?.key} size={2} />
											<Select label="Typ" accessor={this.formState.field("accountType")} size={3} />
											<Select label="Segment" accessor={this.formState.field("clientSegment")} size={3} />
										</FieldRow>
										{/*
										<FieldRow>
											<Select label="Hauptkontakt" accessor={this.formState.field("mainContact")} size={6} />
											<MultiSelect label="Bereiche" accessor={this.formState.field("area")} size={6} />
										</FieldRow>
										*/}
										<FieldRow>
											<TextArea label="Beschreibung" accessor={this.formState.field("description")} rows={12} />
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
