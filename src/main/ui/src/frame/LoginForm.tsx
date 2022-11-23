
import { Button, Card, Checkbox, MediaObject } from "@salesforce/design-system-react";
import { COMMUNITY_TENANT, Config, Enumerated, LoginTenantInfo, LoginUserInfo, LOGIN_INFO_ITEM, session, Session } from "@zeitwert/ui-model";
import { computed, makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React, { ChangeEvent } from "react";
import ReactMarkdown from "react-markdown";
import disclaimer from "./pilot/disclaimer";

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

interface LoginInfo {
	hasAcceptedDisclaimer?: boolean;
}

@inject("appStore", "session")
@observer
export default class LoginForm extends React.Component<LoginFormProps> {

	@observable email: string | undefined = undefined;
	@observable password: string | undefined = undefined;

	@observable userInfo: LoginUserInfo | undefined = undefined;
	@observable.ref tenants: Enumerated[] = [];
	@observable tenant: Enumerated | undefined = undefined;

	@observable tenantInfo: LoginTenantInfo | undefined = undefined;
	@observable.ref accounts: Enumerated[] = [];
	@observable account: Enumerated | undefined = undefined;

	@observable didAcceptDisclaimer: boolean = false;
	@observable hasLoginFailed: boolean = false;

	@computed get isEmailValid(): boolean {
		return !!this.email && /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i.test(this.email);
	}

	@computed get needAccount(): boolean {
		return !!this.tenantInfo && !!this.userInfo?.role && ["user", "super_user", "read_only"].indexOf(this.userInfo?.role.id) >= 0;
	}

	@computed get logoUrl(): string | undefined {
		if (!!this.account) {
			return Config.getRestUrl("account", `accounts/${this.account.id}/logo`);
		} else if (!!this.tenantInfo) {
			return Config.getRestUrl("oe", `tenants/${this.tenantInfo.id}/logo`);
		}
		return "/zw-logo.jpg";
	}

	@computed get needDisclaimer(): boolean {
		return !!this.tenantInfo && this.tenantInfo.tenantType.id === COMMUNITY_TENANT;
	}

	@computed get isReadyForLogin(): boolean {
		if (this.needDisclaimer && !this.didAcceptDisclaimer) {
			return false;
		}
		return this.isEmailValid && !!this.userInfo?.role && !!this.password && !!this.tenant && (!this.needAccount || !!this.account);
	}

	constructor(props: LoginFormProps) {
		super(props);
		makeObservable(this);
		this.didAcceptDisclaimer = this.hasAcceptedDisclaimer();
	}

	render() {
		return (
			<div className="slds-grid slds-wrap slds-m-top_xx-large" style={{ marginTop: "15em" }}>
				<div className="slds-col slds-size_1-of-3" />
				<div className="slds-col slds-size_1-of-3">
					<Card heading={CARD_HEADER}>
						<form id="login" onSubmit={(evt: any) => this.login(evt, "form")}>
							<div className="slds-grid slds-wrap">
								<div className="slds-col slds-size_2-of-3">
									<div className="slds-card__body slds-card__body_inner">
										<div className="slds-form-element">
											<label className="slds-form-element__label" htmlFor="email">Email <abbr className="slds-required"></abbr></label>
											<div className="slds-form-element__control">
												<input type="text" id="username" name="username" autoComplete="username" placeholder="Email…" className="slds-input" onChange={this.setEmail} />
											</div>
										</div>
										<div className="slds-form-element">
											<label className="slds-form-element__label" htmlFor="password">Passwort <abbr className="slds-required"></abbr></label>
											<div className="slds-form-element__control">
												<input type="password" id="password" name="password" autoComplete="current-password" placeholder="Passwort…" className="slds-input" onChange={this.setPassword} />
											</div>
										</div>
										{
											this.tenants?.length > 1 &&
											<div className="slds-form-element">
												<label className="slds-form-element__label" htmlFor="tenant">Mandant <abbr className="slds-required"></abbr></label>
												<div className="slds-form-element__control">
													<div className="slds-select_container">
														<select className="slds-select" id="tenant" onChange={(e) => this.setTenant(e.target.value)}>
															<option value="">Mandant…</option>
															{
																this.tenants.map(t => <option value={t.id} key={"tenant-" + t.id}>{t.name}</option>)
															}
														</select>
													</div>
												</div>
											</div>
										}
										{
											this.needAccount && this.accounts?.length > 1 &&
											<div className="slds-form-element">
												<label className="slds-form-element__label" htmlFor="account">Kunde <abbr className="slds-required"></abbr></label>
												<div className="slds-form-element__control">
													<div className="slds-select_container">
														<select className="slds-select" id="account" onChange={(e) => this.setAccount(e.target.value)}>
															<option value="">Kunde…</option>
															{
																this.accounts.map(a => <option value={a.id} key={"acct-" + a.id}>{a.name}</option>)
															}
														</select>
													</div>
												</div>
											</div>
										}
										{
											this.needDisclaimer &&
											<>
												<div className="slds-form-element slds-m-top_medium">
													<div className="slds-notify slds-notify_alert" role="alert">
														<span className="slds-icon_container slds-icon-utility-warning slds-m-right_x-small" title="Description of icon when needed">
															<svg className="slds-icon slds-icon_x-small" aria-hidden="true">
																<use xlinkHref="/assets/icons/utility-sprite/svg/symbols.svg#warning"></use>
															</svg>
														</span>
														<h2><b>Pilotvereinbarung</b></h2>
													</div>
													<ReactMarkdown className="fa-note-content slds-m-top_medium">
														{disclaimer}
													</ReactMarkdown>
													<div className="slds-form-element slds-m-top_medium">
														<Checkbox
															id="accept-disclaimer"
															labels={{ label: "Ich akzeptiere die Pilotvereinbarung" }}
															checked={this.didAcceptDisclaimer}
															onChange={this.acceptDisclaimer}
															disabled={this.didAcceptDisclaimer}
														/>
													</div>
												</div>
											</>
										}
									</div>
								</div>
								<div className="slds-col slds-size_1-of-3">
									<div className="slds-card__body slds-card__body_inner" style={{ marginTop: "34px", marginRight: "1rem" }}>
										<img src={this.logoUrl} alt="Logo" style={{ height: "144px" }} />
									</div>
								</div>
							</div>
							{
								this.hasLoginFailed &&
								<LoginAlert clearError={() => this.hasLoginFailed = false} />
							}
							<footer className="slds-card__footer">
								<Button
									label="Login"
									type="submit"
									variant="brand"
									disabled={!this.isReadyForLogin}
									onClick={(evt: any) => this.login(evt, "button")}
								/>
							</footer>
						</form>
					</Card>
				</div >
				<div className="slds-col slds-size_1-of-3" />
			</div >
		);
	}

	private setEmail = async (email: ChangeEvent<HTMLInputElement>) => {
		this.email = email.target.value;
		this.userInfo = undefined;
		this.tenants = [];
		this.tenant = undefined;
		this.tenantInfo = undefined;
		this.accounts = [];
		this.account = undefined;
		this.hasLoginFailed = false;
		if (this.isEmailValid) {
			this.userInfo = await session.userInfo(this.email);
			this.tenants = this.userInfo?.tenants!;
			if (this.tenants?.length === 1) {
				this.setTenant(this.tenants[0].id);
			}
		}
	}

	private setPassword = (password: ChangeEvent<HTMLInputElement>) => {
		this.password = password.target.value;
		this.hasLoginFailed = false;
	}

	private setTenant = async (tenantId: string) => {
		this.tenant = this.tenants.find(t => t.id === tenantId);
		this.tenantInfo = undefined;
		this.accounts = [];
		this.account = undefined;
		this.hasLoginFailed = false;
		if (!!this.tenant) {
			this.tenantInfo = await session.tenantInfo(this.tenant.id);
			this.accounts = this.tenantInfo?.accounts!;
			if (this.accounts?.length === 1) {
				this.setAccount(this.accounts[0].id);
			}
		}
	}

	private setAccount = (accountId: string) => {
		this.account = this.accounts.find(a => a.id === accountId);
		this.hasLoginFailed = false;
	}

	private getLoginInfo = (): LoginInfo => {
		const loginInfo = localStorage.getItem(LOGIN_INFO_ITEM);
		if (loginInfo) {
			return JSON.parse(loginInfo);
		}
		return {};
	}

	private hasAcceptedDisclaimer = (): boolean => {
		return !!this.getLoginInfo().hasAcceptedDisclaimer;
	}

	private acceptDisclaimer = (): void => {
		const loginInfo = this.getLoginInfo();
		loginInfo.hasAcceptedDisclaimer = true;
		localStorage.setItem(LOGIN_INFO_ITEM, JSON.stringify(loginInfo));
		this.didAcceptDisclaimer = this.hasAcceptedDisclaimer();
	}

	private login = async (evt: any, src: string) => {
		if (!this.isReadyForLogin) {
			return false;
		} else if (src === "button") {
			return false;
		}
		evt.preventDefault();
		await session.login(this.email!, this.password!, this.tenant, this.account);
		if (!session.isAuthenticated) {
			this.hasLoginFailed = true;
		} else {
			if (window["PasswordCredential"]) {
				var c = new window["PasswordCredential"](evt.target);
				return navigator.credentials.store(c);
			}
			window.history.replaceState({}, "", "/");
		}
	}

}

const LoginAlert = (props: { clearError: () => void }) => {
	return <div className="slds-notify slds-notify_alert slds-alert_error" role="alert">
		<span className="slds-assistive-text">error</span>
		<span className="slds-icon_container slds-icon-utility-error slds-m-right_x-small">
			<svg className="slds-icon slds-icon_x-small" aria-hidden="true">
				<use xlinkHref="/assets/icons/utility-sprite/svg/symbols.svg#error"></use>
			</svg>
		</span>
		<h2>Anmeldung nicht erfolgreich!</h2>
		<div className="slds-notify__close">
			<button className="slds-button slds-button_icon slds-button_icon-small slds-button_icon-inverse" title="Close" onClick={props.clearError}>
				<svg className="slds-button__icon" aria-hidden="true">
					<use xlinkHref="/assets/icons/utility-sprite/svg/symbols.svg#close"></use>
				</svg>
				<span className="slds-assistive-text">Close</span>
			</button>
		</div>
	</div>
};
