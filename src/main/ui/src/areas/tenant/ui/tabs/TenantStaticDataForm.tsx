
import { Card } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow, Input, Select, SldsForm, TextArea } from "@zeitwert/ui-forms";
import { ACCOUNT_API, Enumerated, TenantModelType, TenantStore, USER_API } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import { FormStateOptions } from "mstform";
import React from "react";
import TenantForm from "../forms/TenantForm";


export interface TenantStaticDataFormProps {
	store: TenantStore;
}

@observer
export default class TenantStaticDataForm extends React.Component<TenantStaticDataFormProps> {

	formStateOptions: FormStateOptions<TenantModelType> = {
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
	};

	@observable
	users: Enumerated[] = [];

	@observable
	accounts: Enumerated[] = [];

	constructor(props: TenantStaticDataFormProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		const users = await USER_API.getAggregates("filter[tenantId]=" + this.props.store.id);
		this.users = Object.values(users.user || {})?.map(user => {
			return {
				id: user.id,
				name: user.caption,
				itemType: user.meta.itemType
			}
		});
		const accounts = await ACCOUNT_API.getAggregates("filter[tenantId]=" + this.props.store.id);
		this.accounts = Object.values(accounts.account || {}).map(acct => {
			return {
				id: acct.id,
				name: acct.caption,
				itemType: acct.meta.itemType
			}
		});
	}

	render() {
		const isInTrx = this.props.store.isInTrx;
		return (
			<SldsForm formModel={TenantForm} formStateOptions={this.formStateOptions} item={this.props.store.tenant!}>
				<Grid className="slds-wrap slds-m-top_small" isVertical={false}>
					<Col cols={1} totalCols={2}>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Grunddaten">
								<FieldRow>
									<Input label="Name" type="text" fieldName="name" />
								</FieldRow>
								<FieldRow>
									<Select label="Typ" fieldName="tenantType" size={8} />
									<Input label="Key" fieldName="key" size={4} />
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
				<Grid className="slds-wrap slds-m-top_small" isVertical={false}>
					<Col cols={1} totalCols={1}>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Berechnungsparameter">
								<FieldRow>
									<Input label="Inflationsrate (in %)" fieldName="inflationRate" size={3} />
								</FieldRow>
							</FieldGroup>
						</Card>
					</Col>
				</Grid>
				{
					!isInTrx &&
					<Grid className="slds-wrap slds-m-top_small" isVertical={false}>
						<Col cols={1} totalCols={2}>
							<Card heading="Benutzer" bodyClassName="slds-m-around_medium">
								<div className="slds-card__body xslds-card__body_inner" style={{ maxHeight: "400px", overflowY: "auto" }}>
									<table className="slds-table slds-table_cell-buffer slds-table_bordered">
										<thead>
											<tr className="slds-line-height_reset">
												<th className="" scope="col">
													<div className="slds-truncate" title="Benutzer">Benutzer</div>
												</th>
											</tr>
										</thead>
										<tbody>
											{
												this.users.map(item => (
													<tr className="slds-hint-parent" key={"include-" + item.id}>
														<th data-label="Benutzer" scope="row">
															<div className="slds-truncate">
																<a href={`/user/${item.id}`} tabIndex={-1}>{item.name}</a>
															</div>
														</th>
													</tr>
												))
											}
										</tbody>
									</table>
								</div>
							</Card>
						</Col>
						<Col cols={1} totalCols={2}>
							<Card heading="Kunden" bodyClassName="slds-m-around_medium">
								<div className="slds-card__body xslds-card__body_inner" style={{ maxHeight: "400px", overflowY: "auto" }}>
									<table className="slds-table slds-table_cell-buffer slds-table_bordered">
										<thead>
											<tr className="slds-line-height_reset">
												<th className="" scope="col">
													<div className="slds-truncate" title="Kunde">Kunde</div>
												</th>
											</tr>
										</thead>
										<tbody>
											{
												this.accounts.map(item => (
													<tr className="slds-hint-parent" key={"exclude-" + item.id}>
														<th data-label="Kunde" scope="row">
															<div className="slds-truncate">
																<a href={`/account/${item.id}`} tabIndex={-1}>{item.name}</a>
															</div>
														</th>
													</tr>
												))
											}
										</tbody>
									</table>
								</div>
							</Card>
						</Col>
					</Grid>
				}
			</SldsForm>
		);
	}

}
