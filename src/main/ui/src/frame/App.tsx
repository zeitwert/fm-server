import { BrandBand, Settings } from "@salesforce/design-system-react";
import { AppStore, Session } from "@zeitwert/ui-model";
import DynamicView from "frame/app/DynamicView";
import { Navigator } from "frame/app/Navigation";
import AppHeader from "frame/ui/AppHeader";
import RedirectItemView from "item/ui/RedirectItemView";
import { Logger } from "loglevel";
import { inject, observer } from "mobx-react";
import React from "react";
import { Helmet } from "react-helmet";
import { Route, Routes } from "react-router-dom";

export interface AppProps {
	isInit: boolean;
}

export interface AppCtx {
	appStore: AppStore;
	logger: Logger;
	navigator: Navigator;
	session: Session;
	showToast: (variant: string, message: string) => void;
	showAlert: (variant: string, message: string) => void;
}

@inject("appStore", "logger", "session")
@observer
export default class App extends React.Component<AppProps> {

	get ctx() {
		return this.props as any as AppCtx;
	}

	render() {
		// @ts-ignore
		(Settings as Settings).setAppElement("#root");
		let content = <div />;
		if (this.props.isInit) {
			content =
				<Routes>
					<Route path="/*" element={<DynamicView />} />
					<Route path="/doc/:itemId" element={<RedirectItemView itemType="doc" />} />
					<Route path="/obj/:itemId" element={<RedirectItemView itemType="obj" />} />
					<Route path="/:path/*" element={<DynamicView />} />
				</Routes>;
		}
		let title = "Login";
		if (this.ctx.session?.sessionInfo?.account) {
			title = this.ctx.session?.sessionInfo?.account.name + " | " + this.ctx.session.appInfo?.name;
		} else if (this.ctx.session?.sessionInfo?.tenant) {
			title = this.ctx.session?.sessionInfo?.tenant.name + " | " + this.ctx.session.appInfo?.name;
		}
		return (
			<>
				<Helmet>
					<title>{title}</title>
					<link
						rel="shortcut icon"
						href={
							this.ctx.session.appInfo?.id
								? `/favicon-${this.ctx.session.appInfo?.id}.png`
								: "/favicon.png"
						}
					/>
				</Helmet>
				<AppHeader />
				<section className="fa-page">
					<BrandBand
						id="brand-band-lightning-blue"
						theme="lightning-blue"
						className="slds-p-around_small slds-scrollable_y"
					>
						{content}
					</BrandBand>
				</section>
			</>
		);
	}

}
