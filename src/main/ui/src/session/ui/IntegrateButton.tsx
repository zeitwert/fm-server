import Button from "@salesforce/design-system-react/components/button";
import { AppCtx } from "App";
import { makeObservable, observable } from "mobx";
import { inject } from "mobx-react";
import React from "react";

interface IntegrateButtonProps {
	disabled?: boolean;
}

@inject("logger", "session")
export default class IntegrateButton extends React.Component<IntegrateButtonProps> {
	@observable authenticationLoading = false;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: IntegrateButtonProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const emailProvider = this.ctx.session.sessionInfo!.user.emailProvider;
		return (
			<Button
				key={"integrate-" + emailProvider.name?.toLowerCase()}
				label={"Connect with " + emailProvider.name}
				className="slds-size_full slds-m-bottom_medium"
				disabled={this.authenticationLoading}
				onClick={() =>
					this.handleExternalIdPAuthentication(this.ctx.session.sessionInfo!.user.emailProvider.id)
				}
			/>
		);
	}

	private handleExternalIdPAuthentication = (provider: string) => {
		this.authenticationLoading = true;
/*
		this.ctx.session
			.handleExternalIdPAuthentication(this.ctx.session.sessionInfo!.user.email, provider)
			.then(() => {
				this.authenticationLoading = false;
			})
			.catch((error) => {
				this.authenticationLoading = false;
				this.ctx.logger.error("Could not external authenticate:", error);
			});
*/
		};
}
