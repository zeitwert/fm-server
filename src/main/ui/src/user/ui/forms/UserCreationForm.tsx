
import { Card } from "@salesforce/design-system-react";
import { EnumeratedField, FieldGroup, FieldRow, Input, Select, TextArea, TextField } from "@zeitwert/ui-forms";
import { session, UserModel, UserStore } from "@zeitwert/ui-model";
import { observer } from "mobx-react";
import { converters, Field, Form } from "mstform";
import React from "react";

const UserCreationFormModel = new Form(
	UserModel,
	{
		id: new Field(converters.string),
		tenant: new EnumeratedField({ source: "{{enumBaseUrl}}/oe/objTenant", required: true }),
		owner: new EnumeratedField({ required: true, source: "{{enumBaseUrl}}/oe/objUser" }),
		email: new TextField({ required: true }),
		password: new TextField({ required: true }),
		role: new TextField({ required: true }),
		name: new TextField({ required: true }),
		description: new TextField(),
	}
);

export interface UserCreationFormProps {
	store: UserStore;
}

@observer
export default class UserCreationForm extends React.Component<UserCreationFormProps> {

	formState: typeof UserCreationFormModel.FormStateType;

	constructor(props: UserCreationFormProps) {
		super(props);
		this.formState = UserCreationFormModel.state(
			this.props.store.item!,
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
					const user = this.props.store.item!;
					if (["tenant"].indexOf(accessor.fieldref) >= 0) {
						return !session.isKernelTenant || !!accessor.value;
					}
					return !!accessor.fieldref && !user.tenant;
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
						<Card heading="Grunddaten" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup>
										<FieldRow>
											<Select label="Mandant" accessor={this.formState.field("tenant")} size={6} />
											<Select label="Verantwortlich" accessor={this.formState.field("owner")} size={6} />
										</FieldRow>
										<FieldRow>
											<Input label="Email" type="text" accessor={this.formState.field("email")} size={6} />
											<Input label="Rolle" type="text" accessor={this.formState.field("role")} size={6} />
										</FieldRow>
										<FieldRow>
											<Input label="Name" type="text" accessor={this.formState.field("name")} size={6} />
											<Input label="Initiales Passwort" type="text" accessor={this.formState.field("password")} size={6} />
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
