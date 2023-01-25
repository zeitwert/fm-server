
import { GlobalHeader, GlobalHeaderSearch } from "@salesforce/design-system-react";
import { session } from "@zeitwert/ui-model";
import { observer } from "mobx-react";
import React from "react";
import AppNavigation from "./AppNavigation";
import Help from "./header/Help";
import Profile from "./header/Profile";
import SearchBar from "./header/SearchBar";
import Setup from "./header/Setup";

@observer
export default class AppHeader extends React.Component {

	render() {
		if (!session.sessionInfo) {
			return <></>;
		}
		return (
			<GlobalHeader
				logoSrc={session.bannerUrl}
				navigation={<AppNavigation />}
				onSkipToContent={() => console.log(">>> Skip to Content Clicked")}
				onSkipToNav={() => console.log(">>> Skip to Nav Clicked")}
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
