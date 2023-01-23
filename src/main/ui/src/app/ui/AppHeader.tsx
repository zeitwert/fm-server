
import { GlobalHeader, GlobalHeaderSearch } from "@salesforce/design-system-react";
import { session } from "@zeitwert/ui-model";
import { AppCtx } from "app/App";
import { inject, observer } from "mobx-react";
import React from "react";
import AppNavigation from "./AppNavigation";
import Help from "./header/Help";
import Profile from "./header/Profile";
import SearchBar from "./header/SearchBar";
import Setup from "./header/Setup";

@inject("appStore", "logger")
@observer
export default class AppHeader extends React.Component {

	get ctx() {
		return this.props as any as AppCtx;
	}

	render() {
		if (!session.sessionInfo) {
			return <></>;
		}
		return (
			<GlobalHeader
				logoSrc={session.bannerUrl}
				navigation={<AppNavigation />}
				onSkipToContent={() => this.ctx.logger.debug(">>> Skip to Content Clicked")}
				onSkipToNav={() => this.ctx.logger.debug(">>> Skip to Nav Clicked")}
			>
				<GlobalHeaderSearch combobox={<SearchBar />} />
				{/*<RecentItems store={this.ctx.appStore} user={session.sessionInfo!.user} />*/}
				{/*<FrequentItems store={this.ctx.appStore} user={session.sessionInfo!.user} />*/}
				<Help />
				<Setup />
				<Profile />
			</GlobalHeader>
		);
	}

}
