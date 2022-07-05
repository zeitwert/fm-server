
import { Button, Card, MediaObject } from "@salesforce/design-system-react";
import { Enumerated, session, Session, TenantInfo } from "@zeitwert/ui-model";
import { computed, makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React, { ChangeEvent } from "react";

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

	@observable email: string | undefined = undefined;
	@observable password: string | undefined = undefined;
	@observable account: Enumerated | undefined = undefined;

	@observable tenant: TenantInfo | undefined = undefined;
	@observable.ref accounts: Enumerated[] = [];

	@computed get isEmailValid(): boolean {
		return !!this.email && /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i.test(this.email);
	}

	@computed get tenantLogoUrl(): string | undefined {
		return !!this.tenant ? `/tenant/${this.tenant.extlKey}/login-logo.jpg` : "/tenant/login-logo.jpg";
	}

	@computed get isReadyForLogin(): boolean {
		return this.isEmailValid && !!this.password && !!this.account;
	}

	constructor(props: LoginFormProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		return (
			<div className="slds-grid slds-wrap slds-m-top_xx-large" style={{ marginTop: "15em" }}>
				<div className="slds-col slds-size_1-of-3" />
				<div className="slds-col slds-size_1-of-3">
					<Card heading={CARD_HEADER}>
						<div className="slds-grid slds-wrap">
							<div className="slds-col slds-size_2-of-3">
								<div className="slds-card__body slds-card__body_inner">
									<div className="slds-form-element">
										<label className="slds-form-element__label" htmlFor="email">Email <abbr className="slds-required"></abbr></label>
										<div className="slds-form-element__control">
											<input type="text" id="email" placeholder="email address…" className="slds-input" onChange={this.setEmail} />
										</div>
									</div>
									<div className="slds-form-element">
										<label className="slds-form-element__label" htmlFor="password">Passwort <abbr className="slds-required"></abbr></label>
										<div className="slds-form-element__control">
											<input type="password" id="password" placeholder="passwort…" className="slds-input" onChange={this.setPassword} />
										</div>
									</div>
									{
										this.accounts?.length > 1 &&
										<div className="slds-form-element">
											<label className="slds-form-element__label" htmlFor="account">Kunde <abbr className="slds-required"></abbr></label>
											<div className="slds-form-element__control">
												<div className="slds-select_container">
													<select className="slds-select" id="account" onChange={this.setAccount}>
														<option value="">Kunde…</option>
														{
															this.accounts.map(a => <option value={a.id}>{a.name}</option>)
														}
													</select>
												</div>
											</div>
										</div>
									}
								</div>
							</div>
							<div className="slds-col slds-size_1-of-3">
								<div className="slds-card__body slds-card__body_inner" style={{ marginTop: "34px", marginRight: "1rem" }}>
									<img src={this.tenantLogoUrl} alt="Tenant Logo" style={{ height: "144px" }} />
								</div>
							</div>
						</div>
						<footer className="slds-card__footer">
							<Button
								label="Login"
								type="submit"
								variant="brand"
								disabled={!this.isReadyForLogin}
								onClick={this.login}
							/>
						</footer>
					</Card>
				</div >
				<div className="slds-col slds-size_1-of-3" />
			</div >
		);
	}

	private setEmail = async (email: ChangeEvent<HTMLInputElement>) => {
		this.email = email.target.value;
		this.tenant = undefined;
		this.accounts = [];
		this.account = undefined;
		if (this.isEmailValid) {
			const userInfo = await session.userInfo(this.email);
			this.tenant = userInfo?.tenant;
			this.accounts = userInfo?.accounts || [];
			if (this.accounts?.length === 1) {
				this.account = this.accounts[0];
			}
		}
	}

	private setPassword = (password: ChangeEvent<HTMLInputElement>) => {
		this.password = password.target.value;
	}

	private setAccount = (account: ChangeEvent<HTMLSelectElement>) => {
		this.account = this.accounts.find(a => account.target.value === a.id);
	}

	private login = async () => {
		if (this.isReadyForLogin) {
			await session.login(this.email!, this.password!, this.account);
			if (!session.isAuthenticated) {
				alert("Could not log in!");
			} else {
				window.location.href = "/";
			}
		}
	}

}
