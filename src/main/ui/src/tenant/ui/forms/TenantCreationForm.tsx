
import { Card } from "@salesforce/design-system-react";
import { EnumeratedField, FieldGroup, FieldRow, Input, Select, TextArea, TextField } from "@zeitwert/ui-forms";
import { TenantModel, TenantStore } from "@zeitwert/ui-model";
import { observer } from "mobx-react";
import { converters, Field, Form } from "mstform";
import React from "react";

const TenantCreationFormModel = new Form(
	TenantModel,
	{
		id: new Field(converters.string),
		name: new TextField({ required: true }),
		description: new TextField(),
		//
		tenantType: new EnumeratedField({ source: "{{enumBaseUrl}}/oe/codeTenantType", required: true }),
	}
);

export interface TenantCreationFormProps {
	store: TenantStore;
}

@observer
export default class TenantCreationForm extends React.Component<TenantCreationFormProps> {

	formState: typeof TenantCreationFormModel.FormStateType;

	constructor(props: TenantCreationFormProps) {
		super(props);
		const tenant = props.store.item!;
		this.formState = TenantCreationFormModel.state(
			tenant,
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
					if (accessor.fieldref && accessor.fieldref !== "tenantType") {
						return !tenant.tenantType;
					}
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
											<Select label="Typ" accessor={this.formState.field("tenantType")} />
										</FieldRow>
										<FieldRow>
											<Input label="Name" type="text" accessor={this.formState.field("name")} />
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
