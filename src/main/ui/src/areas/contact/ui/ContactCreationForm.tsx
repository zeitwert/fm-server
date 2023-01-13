
import { Card } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow, Input, Select, SldsForm } from "@zeitwert/ui-forms";
import { ContactModel, ContactModelType, ContactStore } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { observer } from "mobx-react";
import { Form, FormStateOptions } from "mstform";
import React from "react";
import ContactFormDef from "./forms/ContactFormDef";

export interface ContactCreationFormProps {
	store: ContactStore;
}

const ContactForm = new Form(
	ContactModel,
	ContactFormDef
);

@observer
export default class ContactCreationForm extends React.Component<ContactCreationFormProps> {

	formStateOptions: FormStateOptions<ContactModelType> = {
	};

	render() {
		return (
			<SldsForm
				formModel={ContactForm}
				formStateOptions={this.formStateOptions}
				item={this.props.store.contact!}
			>
				<Grid className="slds-wrap slds-m-top_small" isVertical={false}>
					<Col cols={1} totalCols={1}>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Grunddaten">
								<FieldRow>
									<Select label="Anrede" fieldName="salutation" size={2} />
									<Input label="Vorname" type="text" fieldName="firstName" size={5} />
									<Input label="Nachname" type="text" fieldName="lastName" size={5} />
								</FieldRow>
								<FieldRow>
									<Select label="Rolle" fieldName="contactRole" />
								</FieldRow>
								<FieldRow>
									<Input label="Email" type="text" fieldName="email" size={6} />
									<Input label="Mobile" type="text" fieldName="mobile" size={3} />
									<Input label="Festnetz" type="text" fieldName="phone" size={3} />
								</FieldRow>
							</FieldGroup>
						</Card>
					</Col>
				</Grid>
			</SldsForm>
		);
	}

}
