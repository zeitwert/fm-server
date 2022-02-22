
import Button from "@salesforce/design-system-react/components/button";
import Card from "@salesforce/design-system-react/components/card";
import MediaObject from "@salesforce/design-system-react/components/media-object";
import { Input, Select } from "@zeitwert/ui-forms";
import { Session } from "@zeitwert/ui-model";
import { inject, observer } from "mobx-react";
import { Instance } from "mobx-state-tree";
import { AnyFormState } from "mstform";
import React from "react";
import { LoginData, LoginFormModel, LoginModel } from "session/model/LoginModel";

export interface LoginFormProps {
	session: Session;
}

const CARD_HEADER =
	<MediaObject
		figure={<img alt="zeitwert" src="/favicon.png" />}
		body={<div className="slds-card__header-link slds-truncate">Login</div>}
		verticalCenter
		canTruncate
	/>;

@inject("appStore", "session")
@observer
export default class LoginForm extends React.Component<LoginFormProps> {

	private formState: AnyFormState;

	constructor(props: LoginFormProps) {
		super(props);
		this.formState = LoginFormModel.state(LoginData);
		this.formState.field("community").references.autoLoadReaction();
	}

	componentWillUnmount() {
		this.formState.field("community").references.clearAutoLoadReaction();
	}

	render() {
		const loginModel = this.formState.node as Instance<typeof LoginModel>;
		const { session } = this.props;
		return (
			<div className="slds-grid slds-wrap slds-m-top_xx-large" style={{ marginTop: "15em" }}>
				<div className="slds-col slds-size_1-of-3" />
				<div className="slds-col slds-size_1-of-3">
					<Card heading={CARD_HEADER}>
						<div className="slds-card__body slds-card__body_inner">
							<Input label="Email" accessor={this.formState.field("email")} placeholder="email address ..." />
							<Input label="Passwort" accessor={this.formState.field("password")} type="password" placeholder="password ..." />
							<Select label="Gemeinde" accessor={this.formState.field("community")} placeholder="community" />
						</div>
						<footer className="slds-card__footer">
							<Button
								label="Login"
								type="submit"
								variant="brand"
								disabled={!loginModel.isReadyForLogin}
								onClick={() => loginModel.login(session)}
							/>
						</footer>
					</Card>
				</div>
				<div className="slds-col slds-size_1-of-3" />
			</div>
		);
	}

}
