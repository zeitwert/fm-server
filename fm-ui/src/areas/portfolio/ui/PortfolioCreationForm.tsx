
import { Card } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow, Input, Select, SldsForm } from "@zeitwert/ui-forms";
import { asEnumerated, Enumerated, Portfolio, PortfolioModelType, session } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import { FormStateOptions } from "mstform";
import React from "react";
import PortfolioForm from "./forms/PortfolioForm";

export interface PortfolioCreationFormProps {
	portfolio: Portfolio;
}

@observer
export default class PortfolioCreationForm extends React.Component<PortfolioCreationFormProps> {

	formStateOptions: FormStateOptions<PortfolioModelType> = {
		isReadOnly: (accessor) => {
			if (!this.props.portfolio.account) {
				return true;
			}
			return false;
		},
	};

	@observable
	accounts: Enumerated[] = [];

	constructor(props: PortfolioCreationFormProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		const tenantInfo = await session.tenantInfo(session.sessionInfo!.tenant.id);
		if (tenantInfo) {
			this.accounts = tenantInfo.accounts;
		}
	}

	render() {
		const portfolio = this.props.portfolio;
		return (
			<SldsForm
				formModel={PortfolioForm}
				formStateOptions={this.formStateOptions}
				item={this.props.portfolio}
			>
				<Grid className="slds-wrap slds-m-top_small" isVertical={false}>
					<Col cols={1} totalCols={1}>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Inhaber">
								<FieldRow>
									<Select
										label="Kunde"
										required={true}
										value={asEnumerated(portfolio.account)}
										values={this.accounts}
										onChange={(e) => { portfolio.setAccount(e!.id) }}
										disabled={!!portfolio.account?.id}
									/>
								</FieldRow>
							</FieldGroup>
						</Card>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Grunddaten">
								<FieldRow>
									<Input label="Name" type="text" fieldName="name" />
								</FieldRow>
								<FieldRow>
									<Input label="Portfolionummer" type="text" fieldName="portfolioNr" />
								</FieldRow>
							</FieldGroup>
						</Card>
					</Col>
				</Grid>
			</SldsForm>
		);
	}

}
