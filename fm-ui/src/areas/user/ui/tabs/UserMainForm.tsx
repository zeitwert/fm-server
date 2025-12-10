
import { Card } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow, Input, Select, SldsForm, TextArea } from "@zeitwert/ui-forms";
import { Enumerated, TENANT_API, User, UserModelType } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { computed, makeObservable, observable, toJS } from "mobx";
import { observer } from "mobx-react";
import { FormStateOptions } from "mstform";
import React from "react";
import UserForm from "../forms/UserForm";

export interface UserMainFormProps {
	user: User;
	doEdit: boolean;
}

@observer
export default class UserMainForm extends React.Component<UserMainFormProps> {

	formStateOptions: FormStateOptions<UserModelType> = {
		isReadOnly: (accessor) => {
			if (!this.props.doEdit) {
				return true;
			}
			return false;
		},
	};

	@observable
	allTenants: Enumerated[] = [];

	@computed
	get availableTenants(): Enumerated[] {
		const user = this.props.user;
		return this.allTenants
			.filter(t => !user.tenants.find(incl => incl.id === t.id))
			.sort((a, b) => a.name < b.name ? -1 : 1);
	}

	constructor(props: UserMainFormProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		const allTenants = await TENANT_API.getAggregates();
		this.allTenants = Object.values(allTenants.tenant).map(t => {
			return {
				id: t.id,
				name: t.caption,
				itemType: t.meta.itemType
			}
		});
	}

	render() {
		const { doEdit, user } = this.props;
		return (
			<SldsForm
				formModel={UserForm}
				formStateOptions={this.formStateOptions}
				item={this.props.user}
			>
				<Grid className="slds-wrap slds-m-top_small" isVertical={false}>
					<Col cols={1} totalCols={2}>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Grunddaten">
								<FieldRow>
									<Input label="Name" type="text" fieldName="name" />
								</FieldRow>
								<FieldRow>
									<Input label="Email" type="text" fieldName="email" />
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
					<Col cols={1} totalCols={2}>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Autorisierung">
								<FieldRow>
									<Select label="Rolle" fieldName="role" />
								</FieldRow>
							</FieldGroup>
						</Card>
					</Col>
					<Col cols={1} totalCols={2}>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="&nbsp;">
								<FieldRow>
									<div className="slds-form-element">
										<legend className="slds-form-element__label" style={{ whiteSpace: "nowrap" }}>Mandanten</legend>
									</div>
								</FieldRow>
							</FieldGroup>
							<div style={{ maxHeight: "200px", overflowY: "auto" }}>
								<table className="slds-table slds-table_cell-buffer slds-table_bordered">
									<thead>
										<tr className="slds-line-height_reset">
											<th className="" scope="col" style={{ width: "95%" }}>
												<div className="slds-truncate" title="Mandant">Mandant</div>
											</th>
											<th className="" scope="col" style={{ width: "5%" }}>
												<div className="slds-truncate" title="Aktion">Aktion</div>
											</th>
										</tr>
									</thead>
									<tbody>
										{
											user.tenants.map(item => (
												<tr className="slds-hint-parent" key={"t-" + item.id}>
													<th data-label="Mandant" scope="row">
														<div className="slds-truncate">
															<a href={`/${item.itemType?.id.substring(4)}/${item.id}`} tabIndex={-1}>{item.name}</a>
														</div>
													</th>
													<td data-label="Aktion">
														{
															doEdit &&
															<button className="slds-button slds-button_icon slds-button_icon-error" title="Entfernen" onClick={() => { user.removeTenant(item.id) }}>
																<svg className="slds-button__icon" aria-hidden="true">
																	<use xlinkHref="/assets/icons/utility-sprite/svg/symbols.svg#close"></use>
																</svg>
															</button>
														}
													</td>
												</tr>
											))
										}
									</tbody>
								</table>
							</div>
						</Card>
						{
							doEdit &&
							<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
								<FieldGroup>
									<FieldRow>
										<Select
											label="Hinzufügen:"
											value={undefined}
											values={this.availableTenants}
											onChange={(e) => { this.addTenant(e!.id) }}
										/>
									</FieldRow>
								</FieldGroup>
							</Card>
						}
					</Col>
				</Grid>
			</SldsForm>
		);
	}

	private addTenant = (id: string): void => {
		const obj = id ? this.allTenants.find(t => t.id === id) : undefined;
		if (obj) {
			this.props.user.addTenant(toJS(obj));
		}
	}

}
