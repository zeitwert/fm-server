
import { Card } from "@salesforce/design-system-react";
import { EnumeratedField, FieldGroup, FieldRow, Input, TextArea, TextField } from "@zeitwert/ui-forms";
import { UserModel, UserStore } from "@zeitwert/ui-model";
import { observer } from "mobx-react";
import { converters, Field, Form } from "mstform";
import React from "react";

const UserStaticDataFormModel = new Form(
	UserModel,
	{
		id: new Field(converters.string),
		tenant: new EnumeratedField({ source: "{{enumBaseUrl}}/oe/objTenant", required: true }),
		email: new TextField({ required: true }),
		name: new TextField({ required: true }),
		role: new TextField({ required: true }),
		description: new TextField(),
	}
);

export interface UserStaticDataFormProps {
	store: UserStore;
}

@observer
export default class UserStaticDataForm extends React.Component<UserStaticDataFormProps> {

	formState: typeof UserStaticDataFormModel.FormStateType;

	constructor(props: UserStaticDataFormProps) {
		super(props);
		this.formState = UserStaticDataFormModel.state(
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
						<Card heading="Grunddaten" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup>
										<FieldRow>
											<Input label="Name" type="text" accessor={this.formState.field("name")} size={6} />
											<Input label="Email" type="text" accessor={this.formState.field("email")} size={4} />
											<Input label="Rolle" type="text" accessor={this.formState.field("role")} size={2} />
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
