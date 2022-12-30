
import { Card } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow, Input, Select, SldsForm, TextArea } from "@zeitwert/ui-forms";
import { TenantModel, TenantStore } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { observer } from "mobx-react";
import { Form } from "mstform";
import React from "react";
import TenantFormDef from "./def/TenantFormDef";


export interface TenantCreationFormProps {
	store: TenantStore;
}

const TenantForm = new Form(
	TenantModel,
	TenantFormDef
);

@observer
export default class TenantCreationForm extends React.Component<TenantCreationFormProps> {

	render() {
		return (
			<SldsForm formModel={TenantForm} item={this.props.store.tenant!}>
				<Grid className="slds-wrap slds-m-top_small" isVertical={false}>
					<Col cols={1} totalCols={1}>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Grunddaten">
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
						</Card>
					</Col>
				</Grid>
			</SldsForm>
		);
	}

}
