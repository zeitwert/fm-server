
import { Card } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow, Input, Select, SldsForm, TextArea } from "@zeitwert/ui-forms";
import { DatePicker } from "@zeitwert/ui-forms/ui/DatePicker";
import { ContactModelType, ContactStore } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { observer } from "mobx-react";
import { FormStateOptions } from "mstform";
import React from "react";
import ContactForm from "../forms/ContactForm";

export interface ContactStaticDataFormProps {
	store: ContactStore;
}


@observer
export default class ContactStaticDataForm extends React.Component<ContactStaticDataFormProps> {

	formStateOptions: FormStateOptions<ContactModelType> = {
		isReadOnly: (accessor) => {
			if (!this.props.store.isInTrx) {
				return true;
			}
			return false;
		},
	};

	render() {
		return (
			<SldsForm
				formModel={ContactForm}
				formStateOptions={this.formStateOptions}
				item={this.props.store.contact!}
			>
				<Grid className="slds-wrap slds-m-top_small" isVertical={false}>
					<Col cols={1} totalCols={2}>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Grunddaten">
								<FieldRow>
									<Select label="Anrede" fieldName="salutation" size={2} />
									<Select label="Titel" fieldName="title" size={2} />
									<Input label="Vorname" type="text" fieldName="firstName" size={4} />
									<Input label="Nachname" type="text" fieldName="lastName" size={4} />
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
					<Col cols={1} totalCols={2}>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="&nbsp;">
								<FieldRow>
									<TextArea label="Beschreibung" fieldName="description" rows={4} />
								</FieldRow>
							</FieldGroup>
						</Card>
					</Col>
				</Grid>
				<Grid className="slds-wrap slds-m-top_small">
					<Col cols={1} totalCols={1}>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Personell">
								<FieldRow>
									<DatePicker label="Geburtsdatum" fieldName="birthDate" size={4} yearRangeMin={-100} yearRangeMax={1} />
								</FieldRow>
							</FieldGroup>
						</Card>
					</Col>
				</Grid>
			</SldsForm>
		);
	}

}
