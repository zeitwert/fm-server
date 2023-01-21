
import { Card } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow, Input, Select, SldsForm, Static, TextArea } from "@zeitwert/ui-forms";
import { Account, AccountModelType } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { observer } from "mobx-react";
import { FormStateOptions } from "mstform";
import React from "react";
import AccountForm from "../forms/AccountForm";

export interface AccountMainFormProps {
	account: Account;
	doEdit: boolean;
}

@observer
export default class AccountMainForm extends React.Component<AccountMainFormProps> {

	formStateOptions: FormStateOptions<AccountModelType> = {
		isReadOnly: (accessor) => {
			if (!this.props.doEdit) {
				return true;
			} else if (["key"].indexOf(accessor.fieldref) >= 0) {
				return true;
			}
			return false;
		},
	};

	render() {
		const account = this.props.account;
		return (
			<SldsForm
				formModel={AccountForm}
				formStateOptions={this.formStateOptions}
				item={account}
			>
				<Grid className="slds-wrap slds-m-top_small" isVertical={false}>
					<Col cols={1} totalCols={2}>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Grunddaten">
								<FieldRow>
									<Input label="Name" type="text" fieldName="name" size={8} />
									<Static label="Schlüssel" value={account.key} size={4} />
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
					<Col cols={1} totalCols={1}>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Berechnungsparameter">
								<FieldRow>
									<Input label={`Inflationsrate in % (Mandant: ${account.tenantInfo?.inflationRate || 0}%)`} fieldName="inflationRate" size={3} />
								</FieldRow>
							</FieldGroup>
						</Card>
					</Col>
				</Grid>
				<Grid className="slds-wrap slds-m-top_small" isVertical={false}>
					<Col cols={1} totalCols={1}>
						<Card heading="Kontakte" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body xslds-card__body_inner">
								<table className="slds-table slds-table_cell-buffer slds-table_bordered">
									<thead>
										<tr className="slds-line-height_reset">
											<th className="" scope="col" style={{ width: "50%" }}>
												<div className="slds-truncate" title="Name">Name</div>
											</th>
											<th className="" scope="col" style={{ width: "20%" }}>
												<div className="slds-truncate" title="Rolle">Rolle</div>
											</th>
											<th className="" scope="col" style={{ width: "15%" }}>
												<div className="slds-truncate" title="Mobile">Mobile</div>
											</th>
											<th className="" scope="col" style={{ width: "15%" }}>
												<div className="slds-truncate" title="Email">Email</div>
											</th>
										</tr>
									</thead>
									<tbody>
										{
											account.contacts.map(contact => (
												<tr className="slds-hint-parent" key={"include-" + contact.id}>
													<th data-label="Name" scope="row">
														<div className="slds-truncate">
															<a href={`/contact/${contact.id}`} tabIndex={-1}>{contact.caption}</a>
														</div>
													</th>
													<td data-label="Rolle">
														<div className="slds-truncate">{contact.contactRole?.name}</div>
													</td>
													<td data-label="Mobile">
														<div className="slds-truncate">{contact.mobile}</div>
													</td>
													<td data-label="Email">
														<div className="slds-truncate">{contact.email}</div>
													</td>
												</tr>
											))
										}
									</tbody>
								</table>
							</div>
						</Card>
					</Col>
				</Grid>
			</SldsForm>
		);
	}

}
