
import { GlobalHeader, GlobalHeaderSearch } from "@salesforce/design-system-react";
import { AppCtx } from "frame/App";
import { inject, observer } from "mobx-react";
import React from "react";
import AppNavigation from "./AppNavigation";
import Help from "./header/Help";
import Profile from "./header/Profile";
import SearchBar from "./header/SearchBar";
import Setup from "./header/Setup";

@inject("appStore", "logger", "session")
@observer
export default class AppHeader extends React.Component {

	get ctx() {
		return this.props as any as AppCtx;
	}

	render() {
		if (!this.ctx.session.sessionInfo) {
			return <></>;
		}
		let logoSrc: string;
		if (!this.ctx.session.appInfo?.id) {
			logoSrc = "/logo.png";
		} else if (this.ctx.session.sessionInfo.account) {
			logoSrc = `/account/${this.ctx.session.sessionInfo.account.key}/banner.png`;
		} else {
			logoSrc = `/tenant/${this.ctx.session.sessionInfo.tenant.id}/logo.png`;
		}
		return (
			<GlobalHeader
				logoSrc={logoSrc}
				navigation={<AppNavigation />}
				onSkipToContent={() => this.ctx.logger.debug(">>> Skip to Content Clicked")}
				onSkipToNav={() => this.ctx.logger.debug(">>> Skip to Nav Clicked")}
			>
				<GlobalHeaderSearch combobox={<SearchBar />} />
				{/*<RecentItems store={this.ctx.appStore} user={this.ctx.session.sessionInfo!.user} />*/}
				{/*<FrequentItems store={this.ctx.appStore} user={this.ctx.session.sessionInfo!.user} />*/}
				<Help />
				<Setup />
				<Profile session={this.ctx.session} />
			</GlobalHeader>
		);
	}

}
