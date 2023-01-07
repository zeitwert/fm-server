
import { Card } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow, Input, Select, SldsForm, Static, TextArea } from "@zeitwert/ui-forms";
import { Account, AccountModel, AccountModelType, AccountStore } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { observer } from "mobx-react";
import { Form, FormStateOptions } from "mstform";
import React from "react";
import AccountFormDef from "../forms/AccountFormDef";

export interface AccountStaticDataFormProps {
	store: AccountStore;
}

const AccountForm = new Form(
	AccountModel,
	AccountFormDef
);

@observer
export default class AccountStaticDataForm extends React.Component<AccountStaticDataFormProps> {

	formStateOptions: FormStateOptions<AccountModelType> = {
		isReadOnly: (accessor) => {
			if (!this.props.store.isInTrx) {
				return true;
			} else if (["key"].indexOf(accessor.fieldref) >= 0) {
				return true;
			}
			return false;
		},
	};

	render() {
		const account = this.props.store.item! as Account;
		return (
			<SldsForm
				formModel={AccountForm}
				formStateOptions={this.formStateOptions}
				item={this.props.store.account!}
			>
				<Grid className="slds-wrap slds-m-top_small" isVertical={false}>
					<Col cols={1} totalCols={2}>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Grunddaten">
								<FieldRow>
									<Input label="Name" type="text" fieldName="name" size={8} />
									<Static label="Schlüssel" value={this.props.store.account?.key} size={4} />
								</FieldRow>
								<FieldRow>
									<Select label="Typ" fieldName="accountType" size={6} />
									<Select label="Segment" fieldName="clientSegment" size={6} />
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
					<Col cols={1} totalCols={2}>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Berechnungsparameter">
								<FieldRow>
									<Input label={`Inflationsrate in % (Mandant: ${account.tenantInfo?.inflationRate || 0}%)`} fieldName="inflationRate" size={3} />
								</FieldRow>
							</FieldGroup>
						</Card>
					</Col>
				</Grid>
			</SldsForm>
		);
	}

}
