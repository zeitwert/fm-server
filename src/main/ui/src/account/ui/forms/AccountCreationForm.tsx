
import { Card } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow, Input, Select, SldsForm, TextArea } from "@zeitwert/ui-forms";
import { AccountModel, AccountModelType, AccountStore, session } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { observer } from "mobx-react";
import { Form, FormStateOptions } from "mstform";
import React from "react";
import AccountFormDef from "./def/AccountFormDef";

export interface AccountCreationFormProps {
	store: AccountStore;
}

const AccountForm = new Form(
	AccountModel,
	AccountFormDef
);

@observer
export default class AccountCreationForm extends React.Component<AccountCreationFormProps> {

	formStateOptions: FormStateOptions<AccountModelType> = {
		isDisabled: (accessor) => {
			const account = this.props.store.account!;
			if (["tenant"].indexOf(accessor.fieldref) >= 0) {
				return !session.isKernelTenant || !!accessor.value;
			}
			return !!accessor.fieldref && !account.tenant;
		},
	};

	render() {
		return (
			<SldsForm formModel={AccountForm} formStateOptions={this.formStateOptions} item={this.props.store.account!}>
				<Grid className="slds-wrap slds-m-top_small" isVertical={false}>
					<Col cols={1} totalCols={1}>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Grunddaten">
								<FieldRow>
									<Select label="Mandant" fieldName="tenant" size={6} />
									<Select label="Verantwortlich" fieldName="owner" size={6} />
								</FieldRow>
								<FieldRow>
									<Input label="Name" type="text" fieldName="name" size={6} />
									<Select label="Typ" fieldName="accountType" size={3} />
									<Select label="Segment" fieldName="clientSegment" size={3} />
								</FieldRow>
								<FieldRow>
									<TextArea label="Beschreibung" fieldName="description" rows={12} />
								</FieldRow>
							</FieldGroup>
						</Card>
					</Col>
				</Grid>
			</SldsForm>
		);
	}

}
