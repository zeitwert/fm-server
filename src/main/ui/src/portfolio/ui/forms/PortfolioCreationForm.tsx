
import { Card } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow, Input, Select, TextField } from "@zeitwert/ui-forms";
import { Enumerated, Portfolio, PortfolioModel, PortfolioStore, session } from "@zeitwert/ui-model";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import { converters, Field, Form } from "mstform";
import React from "react";

const PortfolioCreationFormModel = new Form(
	PortfolioModel,
	{
		id: new Field(converters.string),
		name: new TextField({ required: true }),
		portfolioNr: new TextField(),
		description: new TextField()
		//
		//portfolioNr: new TextField({ required: true }),
	}
);

export interface PortfolioCreationFormProps {
	store: PortfolioStore;
}

@observer
export default class PortfolioCreationForm extends React.Component<PortfolioCreationFormProps> {

	formState: typeof PortfolioCreationFormModel.FormStateType;

	@observable
	accounts: Enumerated[] = [];

	constructor(props: PortfolioCreationFormProps) {
		super(props);
		makeObservable(this);
		const portfolio = props.store.item!;
		this.formState = PortfolioCreationFormModel.state(
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
					} else if (!portfolio.account) {
						return true;
					}
					return false;
				},
				isDisabled: (accessor) => {
					return false;
				},
			}
		);
	}

	async componentDidMount() {
		const tenantInfo = await session.tenantInfo(session.sessionInfo!.tenant.id);
		if (tenantInfo) {
			this.accounts = tenantInfo.accounts;
		}
	}

	render() {
		const portfolio = this.props.store.item! as Portfolio;
		return (
			<div>
				<div className="slds-grid slds-wrap slds-m-top_small">
					<div className="slds-col slds-size_1-of-1">
						<Card heading="Inhaber" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup>
										<FieldRow>
											<Select
												label="Kunde"
												required={true}
												value={portfolio.account?.id}
												values={this.accounts}
												onChange={(e) => { portfolio.setAccount(e.target.value?.toString()) }}
												disabled={!!portfolio.account?.id}
											/>
										</FieldRow>
									</FieldGroup>
								</div>
							</div>
						</Card>
						<Card heading="Grunddaten" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup>
										<FieldRow>
											<Input label="Name" type="text" accessor={this.formState.field("name")} />
										</FieldRow>
										<FieldRow>
											<Input label="Portfolionummer" type="text" accessor={this.formState.field("portfolioNr")} />
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

}
