
import { Card } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow, Input, Select, SldsForm, TextArea } from "@zeitwert/ui-forms";
import { session, UserModelType, UserStore } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { observer } from "mobx-react";
import { FormStateOptions } from "mstform";
import React from "react";
import UserForm from "./forms/UserForm";

export interface UserCreationFormProps {
	store: UserStore;
}

@observer
export default class UserCreationForm extends React.Component<UserCreationFormProps> {

	formStateOptions: FormStateOptions<UserModelType> = {
		isDisabled: (accessor) => {
			const user = this.props.store.user!;
			if (["tenant"].indexOf(accessor.fieldref) >= 0) {
				return !session.isKernelTenant || !!accessor.value;
			}
			return !!accessor.fieldref && !user.tenant;
		},
	};

	render() {
		return (
			<SldsForm formModel={UserForm} formStateOptions={this.formStateOptions} item={this.props.store.user!}>
				<Grid className="slds-wrap slds-m-top_small" isVertical={false}>
					<Col cols={1} totalCols={1}>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Grunddaten">
								<FieldRow>
									<Select label="Mandant" fieldName="tenant" size={6} />
									<Select label="Verantwortlich" fieldName="owner" size={6} />
								</FieldRow>
								<FieldRow>
									<Input label="Email" type="text" fieldName="email" size={6} />
									<Select label="Rolle" fieldName="role" size={6} />
								</FieldRow>
								<FieldRow>
									<Input label="Name" type="text" fieldName="name" size={6} />
									<Input label="Initiales Passwort" type="text" fieldName="password" size={6} />
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
