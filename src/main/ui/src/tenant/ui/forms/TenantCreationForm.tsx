
import { Card } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow, Input, Select, SldsForm, TextArea } from "@zeitwert/ui-forms";
import { TenantStore } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { observer } from "mobx-react";
import React from "react";
import TenantFormModel from "./TenantFormModel";


export interface TenantCreationFormProps {
	store: TenantStore;
}

@observer
export default class TenantCreationForm extends React.Component<TenantCreationFormProps> {

	render() {
		return (
			<SldsForm formModel={TenantFormModel} item={this.props.store.tenant!}>
				<Grid className="slds-wrap slds-m-top_small" isVertical={false}>
					<Col cols={1} totalCols={1}>
						<Card heading="Grunddaten" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<FieldGroup>
									<FieldRow>
										<Select label="Typ" fieldName="tenantType" />
									</FieldRow>
									<FieldRow>
										<Input label="Name" type="text" fieldName="name" />
									</FieldRow>
									<FieldRow>
										<TextArea label="Beschreibung" fieldName="description" rows={12} />
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
