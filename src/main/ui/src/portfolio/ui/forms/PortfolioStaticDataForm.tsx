
import { Card } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow, Input, Select, TextArea, TextField } from "@zeitwert/ui-forms";
import { ACCOUNT_API, BUILDING_API, Enumerated, Portfolio, PortfolioModel, PortfolioStore, PORTFOLIO_API } from "@zeitwert/ui-model";
import { computed, makeObservable, observable, toJS } from "mobx";
import { observer } from "mobx-react";
import { converters, Field, Form } from "mstform";
import React from "react";

const PortfolioStaticDataFormModel = new Form(
	PortfolioModel,
	{
		id: new Field(converters.string),
		name: new TextField({ required: true }),
		portfolioNr: new TextField(),
		description: new TextField(),
	}
);

export interface PortfolioStaticDataFormProps {
	store: PortfolioStore;
}

@observer
export default class PortfolioStaticDataForm extends React.Component<PortfolioStaticDataFormProps> {

	formState: typeof PortfolioStaticDataFormModel.FormStateType;

	@observable
	allAccounts: Enumerated[] = [];

	@observable
	allPortfolios: Enumerated[] = [];

	@observable
	allBuildings: Enumerated[] = [];

	@computed
	get allObjects(): Enumerated[] {
		return this.allAccounts.concat(this.allPortfolios).concat(this.allBuildings);
	}

	@computed
	get availableAccounts(): Enumerated[] {
		const portfolio = this.props.store.item! as Portfolio;
		return this.allAccounts
			.filter(acct => !portfolio.includes.find(incl => incl.id === acct.id))
			.filter(acct => !portfolio.excludes.find(excl => excl.id === acct.id))
			.sort((a, b) => a.name < b.name ? -1 : 1);
	}

	@computed
	get availablePortfolios(): Enumerated[] {
		const portfolio = this.props.store.item! as Portfolio;
		return this.allPortfolios
			.filter(pf => pf.id !== portfolio.id)
			.filter(pf => !portfolio.includes.find(incl => incl.id === pf.id))
			.filter(pf => !portfolio.excludes.find(excl => excl.id === pf.id))
			.sort((a, b) => a.name < b.name ? -1 : 1);
	}

	@computed
	get availableBuildings(): Enumerated[] {
		const portfolio = this.props.store.item! as Portfolio;
		return this.allBuildings
			.filter(bldg => !portfolio.includes.find(incl => incl.id === bldg.id))
			.filter(bldg => !portfolio.excludes.find(excl => excl.id === bldg.id))
			.sort((a, b) => a.name < b.name ? -1 : 1);
	}

	@computed
	get availableObjects(): Enumerated[] {
		return this.availableAccounts.concat(this.availablePortfolios).concat(this.availableBuildings);
	}

	constructor(props: PortfolioStaticDataFormProps) {
		super(props);
		makeObservable(this);
		const portfolio = props.store.item!;
		this.formState = PortfolioStaticDataFormModel.state(
			portfolio,
			{
				converterOptions: {
					decimalSeparator: ".",
					thousandSeparator: "'",
					renderThousands: true,
				},
				isReadOnly: (accessor) => {
					if (!props.store.isInTrx) {
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
		//this.formState.field("portfolioSubType").references.autoLoadReaction();
	}

	async componentDidMount() {
		const allAccounts = await ACCOUNT_API.getAggregates();
		this.allAccounts = Object.values(allAccounts.account).map(acct => {
			return {
				id: acct.id,
				name: "Kunde: " + acct.caption,
				itemType: acct.meta.itemType
			}
		});
		const allPortfolios = await PORTFOLIO_API.getAggregates();
		this.allPortfolios = Object.values(allPortfolios.portfolio).map(pf => {
			return {
				id: pf.id,
				name: "Portfolio: " + pf.caption,
				itemType: pf.meta.itemType
			}
		});
		const allBuildings = await BUILDING_API.getAggregates();
		this.allBuildings = Object.values(allBuildings.building).map(bldg => {
			return {
				id: bldg.id,
				name: "Immobilie: " + bldg.caption,
				itemType: bldg.meta.itemType
			}
		});
	}

	componentWillUnmount() {
		//this.formState.field("portfolioSubType").references.clearAutoLoadReaction();
	}

	render() {
		const isInTrx = this.props.store.isInTrx;
		const portfolio = this.props.store.item! as Portfolio;
		return (
			<div>
				<div className="slds-grid slds-wrap slds-m-top_small">
					<div className="slds-col slds-size_1-of-2">
						<Card heading="Identifikation" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup>
										<FieldRow>
											<Input label="Name" type="text" accessor={this.formState.field("name")} size={9} />
											<Input label="Portfolio Nr." type="text" accessor={this.formState.field("portfolioNr")} size={3} />
										</FieldRow>
									</FieldGroup>
									<FieldRow>
										<Select
											label="Gemeinde"
											value={portfolio.account?.id}
											values={[{ id: portfolio.account!.id!, name: portfolio.account!.name!, itemType: portfolio.account!.meta?.itemType }]}
											onChange={(e) => { portfolio.setAccount(e.target.value?.toString()) }}
											readOnly={isInTrx}
											disabled={true}
										/>
									</FieldRow>
								</div>
							</div>
						</Card>
					</div>
					<div className="slds-col slds-size_1-of-2">
						<Card heading="Beschreibung" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup>
										<FieldRow>
											<TextArea label="Beschreibung / Kommentare" accessor={this.formState.field("description")} rows={4} />
										</FieldRow>
									</FieldGroup>
								</div>
							</div>
						</Card>
					</div>
				</div>
				<div className="slds-grid slds-wrap slds-m-top_small">
					<div className="slds-col slds-size_1-of-2">
						<Card heading={`Einzuschliessende Elemente (${portfolio.includes.length})`} bodyClassName="slds-m-around_medium">
							<div className="slds-card__body xslds-card__body_inner" style={{ maxHeight: "200px", overflowY: "auto" }}>
								<table className="slds-table slds-table_cell-buffer slds-table_bordered">
									<thead>
										<tr className="slds-line-height_reset">
											<th className="" scope="col" style={{ width: "75%" }}>
												<div className="slds-truncate" title="Element">Element</div>
											</th>
											<th className="" scope="col" style={{ width: "20%" }}>
												<div className="slds-truncate" title="Typ">Typ</div>
											</th>
											<th className="" scope="col" style={{ width: "5%" }}>
												<div className="slds-truncate" title="Aktion">Aktion</div>
											</th>
										</tr>
									</thead>
									<tbody>
										{
											portfolio.includes.map(item => (
												<tr className="slds-hint-parent" key={"include-" + item.id}>
													<th data-label="Element" scope="row">
														<div className="slds-truncate">
															<a href={`/${item.itemType?.id.substring(4)}/${item.id}`} tabIndex={-1}>{item.name}</a>
														</div>
													</th>
													<td data-label="Typ">
														<div className="slds-truncate">{item.itemType?.name}</div>
													</td>
													<td data-label="Aktion">
														{
															isInTrx &&
															<button className="slds-button slds-button_icon slds-button_icon-error" title="Entfernen" onClick={() => { portfolio.removeIncludeObj(item.id) }}>
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
					</div>
					<div className="slds-col slds-size_1-of-2">
						<Card heading={`Auszuschliessende Elemente (${portfolio.excludes.length})`} bodyClassName="slds-m-around_medium">
							<div className="slds-card__body xslds-card__body_inner" style={{ maxHeight: "200px", overflowY: "auto" }}>
								<table className="slds-table slds-table_cell-buffer slds-table_bordered">
									<thead>
										<tr className="slds-line-height_reset">
											<th className="" scope="col" style={{ width: "75%" }}>
												<div className="slds-truncate" title="Element">Element</div>
											</th>
											<th className="" scope="col" style={{ width: "20%" }}>
												<div className="slds-truncate" title="Typ">Typ</div>
											</th>
											<th className="" scope="col" style={{ width: "5%" }}>
												<div className="slds-truncate" title="Aktion">Aktion</div>
											</th>
										</tr>
									</thead>
									<tbody>
										{
											portfolio.excludes.map(item => (
												<tr className="slds-hint-parent" key={"exclude-" + item.id}>
													<th data-label="Element" scope="row">
														<div className="slds-truncate">
															<a href={`/${item.itemType?.id.substring(4)}/${item.id}`} tabIndex={-1}>{item.name}</a>
														</div>
													</th>
													<td data-label="Typ">
														<div className="slds-truncate">{item.itemType?.name}</div>
													</td>
													<td data-label="Aktion">
														{
															isInTrx &&
															<button className="slds-button slds-button_icon slds-button_icon-error" title="Remove" onClick={() => { portfolio.removeExcludeObj(item.id) }}>
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
					</div>
				</div>
				{
					isInTrx &&
					<div className="slds-grid slds-wrap slds-m-top_small">
						<div className="slds-col slds-size_1-of-2">
							<Card heading="Neu einschliessen" bodyClassName="slds-m-around_medium">
								<div className="slds-card__body xslds-card__body_inner">
									<FieldGroup>
										<FieldRow>
											<Select
												label="Kunde / Portfolio / Immobilie:"
												value={undefined}
												values={this.availableObjects}
												onChange={(e) => { this.addIncludeObj(this.allObjects, e.target.value?.toString()) }}
											/>
										</FieldRow>
									</FieldGroup>
								</div>
							</Card>
						</div>
						<div className="slds-col slds-size_1-of-2">
							<Card heading="Neu ausschliessen" bodyClassName="slds-m-around_medium">
								<div className="slds-card__body xslds-card__body_inner">
									<FieldGroup>
										<FieldRow>
											<Select
												label="Kunde / Portfolio / Immobilie:"
												value={undefined}
												values={this.availableObjects}
												onChange={(e) => { this.addExcludeObj(this.allObjects, e.target.value?.toString()) }}
											/>
										</FieldRow>
									</FieldGroup>
								</div>
							</Card>
						</div>
					</div>
				}
				{
					<div className="slds-grid slds-wrap slds-m-top_small">
						<div className="slds-col slds-size_1-of-1">
							<Card heading={`Aktuelles Resultat (${portfolio.buildings.length})`} bodyClassName="slds-m-around_medium">
								<div className="slds-card__body xslds-card__body_inner">
									<table className="slds-table slds-table_cell-buffer slds-table_bordered">
										<thead>
											<tr className="slds-line-height_reset">
												<th className="" scope="col">
													<div className="slds-truncate" title="Element">Element</div>
												</th>
												<th className="" scope="col">
													<div className="slds-truncate" title="Typ">Typ</div>
												</th>
											</tr>
										</thead>
										<tbody>
											{
												portfolio.buildings.map(item => (
													<tr className="slds-hint-parent" key={"bldg-" + item.id}>
														<th data-label="Element" scope="row">
															<div className="slds-truncate">
																<a href={`/${item.itemType?.id.substring(4)}/${item.id}`} tabIndex={-1}>{item.name}</a>
															</div>
														</th>
														<td data-label="Typ">
															<div className="slds-truncate">{item.itemType?.name}</div>
														</td>
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

	private addIncludeObj = (collection: Enumerated[], id: string): void => {
		const obj = id ? collection.find(c => c.id === id) : undefined;
		if (obj) {
			this.props.store.portfolio!.addIncludeObj(toJS(obj));
		}
	}

	private addExcludeObj = (collection: Enumerated[], id: string): void => {
		const obj = id ? collection.find(c => c.id === id) : undefined;
		if (obj) {
			this.props.store.portfolio!.addExcludeObj(toJS(obj));
		}
	}

}
