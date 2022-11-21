
import { Card } from "@salesforce/design-system-react";
import { EnumeratedField, FieldGroup, FieldRow, Input, NumberField, Select, TextArea, TextField } from "@zeitwert/ui-forms";
import { TenantModel, TenantStore } from "@zeitwert/ui-model";
import { observer } from "mobx-react";
import { converters, Field, Form } from "mstform";
import React from "react";

const TenantStaticDataFormModel = new Form(
	TenantModel,
	{
		id: new Field(converters.string),
		key: new TextField(),
		name: new TextField({ required: true }),
		description: new TextField(),
		inflationRate: new NumberField(),
		//
		tenantType: new EnumeratedField({ source: "{{enumBaseUrl}}/oe/codeTenantType", required: true }),
	}
);

export interface TenantStaticDataFormProps {
	store: TenantStore;
}

@observer
export default class TenantStaticDataForm extends React.Component<TenantStaticDataFormProps> {

	formState: typeof TenantStaticDataFormModel.FormStateType;

	constructor(props: TenantStaticDataFormProps) {
		super(props);
		this.formState = TenantStaticDataFormModel.state(
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
					if (["key"].indexOf(accessor.fieldref) >= 0) {
						return true;
					}
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
											<Input label="Name" type="text" accessor={this.formState.field("name")} size={6} />
											<Select label="Typ" accessor={this.formState.field("tenantType")} size={4} />
											<Input label="Key" accessor={this.formState.field("key")} size={2} />
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
