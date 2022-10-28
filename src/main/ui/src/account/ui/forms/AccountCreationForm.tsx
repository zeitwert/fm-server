
import { Card } from "@salesforce/design-system-react";
import { EnumeratedField, FieldGroup, FieldRow, Input, Select, TextArea, TextField } from "@zeitwert/ui-forms";
import { AccountModel, AccountStore, session } from "@zeitwert/ui-model";
import { observer } from "mobx-react";
import { converters, Field, Form } from "mstform";
import React from "react";

const AccountCreationFormModel = new Form(
	AccountModel,
	{
		id: new Field(converters.string),
		tenant: new EnumeratedField({ source: "{{enumBaseUrl}}/oe/objTenant", required: true }),
		owner: new EnumeratedField({ required: true, source: "{{enumBaseUrl}}/oe/objUser" }),
		name: new TextField({ required: true }),
		accountType: new EnumeratedField({ source: "{{enumBaseUrl}}/account/codeAccountType", required: true }),
		clientSegment: new EnumeratedField({ source: "{{enumBaseUrl}}/account/codeClientSegment" }),
		description: new TextField(),
	}
);

export interface AccountCreationFormProps {
	store: AccountStore;
}

@observer
export default class AccountCreationForm extends React.Component<AccountCreationFormProps> {

	formState: typeof AccountCreationFormModel.FormStateType;

	constructor(props: AccountCreationFormProps) {
		super(props);
		this.formState = AccountCreationFormModel.state(
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
					}
					return false;
				},
				isDisabled: (accessor) => {
					const account = this.props.store.item!;
					if (["tenant"].indexOf(accessor.fieldref) >= 0) {
						return !session.isKernelTenant || !!accessor.value;
					}
					return !!accessor.fieldref && !account.tenant;
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
											<Select label="Mandant" accessor={this.formState.field("tenant")} size={6} />
											<Select label="Verantwortlich" accessor={this.formState.field("owner")} size={6} />
										</FieldRow>
										<FieldRow>
											<Input label="Name" type="text" accessor={this.formState.field("name")} size={6} />
											<Select label="Typ" accessor={this.formState.field("accountType")} size={3} />
											<Select label="Segment" accessor={this.formState.field("clientSegment")} size={3} />
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
