
import { Card } from "@salesforce/design-system-react";
import { EnumeratedField, FieldGroup, FieldRow, Input, Select, TextArea, TextField } from "@zeitwert/ui-forms";
import { Enumerated, TENANT_API, User, UserModel, UserStore } from "@zeitwert/ui-model";
import { computed, makeObservable, observable, toJS } from "mobx";
import { observer } from "mobx-react";
import { converters, Field, Form } from "mstform";
import React from "react";

const UserStaticDataFormModel = new Form(
	UserModel,
	{
		id: new Field(converters.string),
		tenant: new EnumeratedField({ source: "{{enumBaseUrl}}/oe/objTenant", required: true }),
		email: new TextField({ required: true }),
		name: new TextField({ required: true }),
		role: new EnumeratedField({ source: "{{enumBaseUrl}}/oe/codeUserRole", required: true }),
		description: new TextField(),
	}
);

export interface UserStaticDataFormProps {
	store: UserStore;
}

@observer
export default class UserStaticDataForm extends React.Component<UserStaticDataFormProps> {

	formState: typeof UserStaticDataFormModel.FormStateType;

	@observable
	allTenants: Enumerated[] = [];

	@computed
	get availableTenants(): Enumerated[] {
		const user = this.props.store.item! as User;
		return this.allTenants
			.filter(t => !user.tenants.find(incl => incl.id === t.id))
			.sort((a, b) => a.name < b.name ? -1 : 1);
	}

	constructor(props: UserStaticDataFormProps) {
		super(props);
		makeObservable(this);
		this.formState = UserStaticDataFormModel.state(
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
					return false;
				},
				isRequired: (accessor) => {
					return false;
				},
			}
		);
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
		const isInTrx = this.props.store.isInTrx;
		const user = this.props.store.item! as User;
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
											<Input label="Email" type="text" accessor={this.formState.field("email")} />
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
					<div className="slds-col slds-size_1-of-2">
						<Card heading="Autorisierung" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body xslds-card__body_inner">
								<FieldGroup>
									<FieldRow>
										<label className="slds-form-element__label" style={{ whiteSpace: "nowrap" }}>Mandanten</label>
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
																isInTrx &&
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
							</div>
						</Card>
						{
							isInTrx &&
							<Card heading="Hinzufügen" bodyClassName="slds-m-around_medium">
								<div className="slds-card__body xslds-card__body_inner">
									<FieldGroup>
										<FieldRow>
											<Select
												label="Mandant:"
												value={undefined}
												values={this.availableTenants}
												onChange={(e) => { this.addTenant(e.target.value?.toString()) }}
											/>
										</FieldRow>
									</FieldGroup>
								</div>
							</Card>
						}
					</div>
					<div className="slds-col slds-size_1-of-2">
						<Card heading="&nbsp;" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup>
										<FieldRow>
											<Select label="Rolle" accessor={this.formState.field("role")} />
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

	private addTenant = (id: string): void => {
		const obj = id ? this.allTenants.find(t => t.id === id) : undefined;
		if (obj) {
			this.props.store.user!.addTenant(toJS(obj));
		}
	}

}
