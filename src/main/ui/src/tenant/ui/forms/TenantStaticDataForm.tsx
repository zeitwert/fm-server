
import { Card } from "@salesforce/design-system-react";
import { EnumeratedField, FieldGroup, FieldRow, Input, NumberField, Select, TextArea, TextField } from "@zeitwert/ui-forms";
import { Tenant, TenantModel, TenantStore } from "@zeitwert/ui-model";
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
		const isInTrx = this.props.store.isInTrx;
		const tenant = this.props.store.item! as Tenant;
		return (
			<div>
				<div className="slds-grid slds-wrap slds-m-top_small">
					<div className="slds-col slds-size_1-of-2">
						<Card heading="Grunddaten" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup>
										<FieldRow>
											<Input label="Name" type="text" accessor={this.formState.field("name")} />
										</FieldRow>
										<FieldRow>
											<Select label="Typ" accessor={this.formState.field("tenantType")} size={8} />
											<Input label="Key" accessor={this.formState.field("key")} size={4} />
										</FieldRow>
									</FieldGroup>
								</div>
							</div>
						</Card>
					</div>
					<div className="slds-col slds-size_1-of-2">
						<Card heading="&nbsp;" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup>
										<FieldRow>
											<TextArea label="Beschreibung" accessor={this.formState.field("description")} rows={4} />
										</FieldRow>
									</FieldGroup>
								</div>
							</div>
						</Card>
					</div>
				</div>
				<div className="slds-grid slds-wrap slds-m-top_small">
					<div className="slds-col slds-size_1-of-1">
						<Card heading="Berechnungsparameter" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup>
										<FieldRow>
											<Input label="Inflationsrate (in %)" accessor={this.formState.field("inflationRate")} size={3} />
										</FieldRow>
									</FieldGroup>
								</div>
							</div>
						</Card>
					</div>
				</div>
				{
					!isInTrx &&
					<div className="slds-grid slds-wrap slds-m-top_small">
						<div className="slds-col slds-size_1-of-2">
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
												tenant.users.map(item => (
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
						</div>
						<div className="slds-col slds-size_1-of-2">
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
												tenant.accounts.map(item => (
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
						</div>
					</div>
				}
			</div>
		);
	}

}
