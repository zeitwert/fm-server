
import { Card } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow, Input, Select, SldsForm, Static, TextArea } from "@zeitwert/ui-forms";
import { Account, AccountStore } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { observer } from "mobx-react";
import { FormStateOptions } from "mstform";
import React from "react";
import AccountFormModel from "./AccountFormModel";

export interface AccountStaticDataFormProps {
	store: AccountStore;
}

@observer
export default class AccountStaticDataForm extends React.Component<AccountStaticDataFormProps> {

	FORM_OPTIONS: FormStateOptions<typeof AccountFormModel> = {
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
			<SldsForm formModel={AccountFormModel} options={this.FORM_OPTIONS} item={this.props.store.account!}>
				<Grid className="slds-wrap slds-m-top_small" isVertical={false}>
					<Col cols={1} totalCols={2}>
						<Card heading="Grunddaten" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<FieldGroup>
									<FieldRow>
										<Input label="Name" type="text" fieldName="name" size={8} />
										<Static label="Schlüssel" value={this.props.store.account?.key} size={4} />
									</FieldRow>
									<FieldRow>
										<Select label="Typ" fieldName="accountType" size={6} />
										<Select label="Segment" fieldName="clientSegment" size={6} />
									</FieldRow>
								</FieldGroup>
							</div>
						</Card>
					</Col>
					<Col cols={1} totalCols={2}>
						<Card heading="&nbsp;" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<FieldGroup>
									<FieldRow>
										<TextArea label="Beschreibung" fieldName="description" rows={4} />
									</FieldRow>
								</FieldGroup>
							</div>
						</Card>
					</Col>
				</Grid>
				<Grid className="slds-wrap slds-m-top_small">
					<Col cols={1} totalCols={2}>
						<Card heading="Berechnungsparameter" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<FieldGroup>
									<FieldRow>
										<Input label={`Inflationsrate in % (Mandant: ${account.tenantInfo?.inflationRate || 0}%)`} fieldName="inflationRate" size={3} />
									</FieldRow>
								</FieldGroup>
							</div>
						</Card>
					</Col>
				</Grid>
			</SldsForm>
		);
	}

}
