
import { BrandBand, Settings } from "@salesforce/design-system-react";
import { session } from "@zeitwert/ui-model";
import DynamicView from "app/frame/DynamicView";
import { Navigator } from "app/frame/Navigation";
import AppHeader from "app/ui/AppHeader";
import RedirectItemView from "lib/item/ui/RedirectItemView";
import { observer } from "mobx-react";
import React from "react";
import { Helmet } from "react-helmet";
import { Route, Routes } from "react-router-dom";

export interface AppProps {
	isInit: boolean;
}

export interface AppCtx {
	navigator: Navigator;
	showToast: (variant: string, message: string) => void;
	showAlert: (variant: string, message: string) => void;
}

@observer
export default class App extends React.Component<AppProps> {

	render() {
		Settings.setAppElement("#root");
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
		if (session?.sessionInfo?.account) {
			title = session?.sessionInfo?.account.name + " | " + session.appInfo?.name;
		} else if (session?.sessionInfo?.tenant) {
			title = session?.sessionInfo?.tenant.name + " | " + session.appInfo?.name;
		}
		return (
			<>
				<Helmet>
					<title>{title}</title>
					<link
						rel="shortcut icon"
						href={
							session.appInfo?.id
								? `/favicon-${session.appInfo?.id}.png`
								: "/favicon.png"
						}
					/>
				</Helmet>
				<AppHeader />
				<section className="fa-page">
					<BrandBand
						image="none"
						className="slds-p-around_small slds-scrollable_y"
						style={{ zIndex: 1, backgroundColor: '#fff' }}
					>
						{content}
					</BrandBand>
				</section>
			</>
		);
	}

}
