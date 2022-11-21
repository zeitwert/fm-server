
import { Card } from "@salesforce/design-system-react";
import { EnumeratedField, FieldGroup, FieldRow, Input, NumberField, Select, Static, TextArea, TextField } from "@zeitwert/ui-forms";
import { AccountModel, AccountStore } from "@zeitwert/ui-model";
import { observer } from "mobx-react";
import { converters, Field, Form } from "mstform";
import React from "react";

const AccountStaticDataFormModel = new Form(
	AccountModel,
	{
		id: new Field(converters.string),
		key: new TextField({ required: true }),
		name: new TextField({ required: true }),
		description: new TextField(),
		inflationRate: new NumberField(),
		//
		accountType: new EnumeratedField({ source: "{{enumBaseUrl}}/account/codeAccountType", required: true }),
		clientSegment: new EnumeratedField({ source: "{{enumBaseUrl}}/account/codeClientSegment" }),
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
											<Static label="Schlüssel" value={this.props.store.account?.key} size={2} />
											<Select label="Typ" accessor={this.formState.field("accountType")} size={3} />
											<Select label="Segment" accessor={this.formState.field("clientSegment")} size={3} />
										</FieldRow>
										<FieldRow>
											<Input label="Inflationsrate (in %)" accessor={this.formState.field("inflationRate")} size={3} />
										</FieldRow>
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
