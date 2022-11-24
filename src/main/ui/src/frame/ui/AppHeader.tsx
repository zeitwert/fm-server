
import { GlobalHeader, GlobalHeaderSearch } from "@salesforce/design-system-react";
import { Config } from "@zeitwert/ui-model";
import { AppCtx } from "frame/App";
import { inject, observer } from "mobx-react";
import React from "react";
import { SvgHeader as AccountSvgHeader } from "./AccountSvgHeader";
import AppNavigation from "./AppNavigation";
import Help from "./header/Help";
import Profile from "./header/Profile";
import SearchBar from "./header/SearchBar";
import Setup from "./header/Setup";
import { SvgHeader as TenantSvgHeader } from "./TenantSvgHeader";

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
			logoSrc = "/zw-banner.jpg";
		} else if (this.ctx.session.sessionInfo.account) {
			logoSrc = Config.getRestUrl("account", `accounts/${this.ctx.session.sessionInfo.account.id}/banner`);
			const svgHeader = AccountSvgHeader.replace("{account}", this.ctx.session.sessionInfo.account.name).replace("{tenant}", this.ctx.session.sessionInfo.tenant.name);
			const logoBlob = new Blob([svgHeader], { type: 'image/svg+xml' });
			logoSrc = URL.createObjectURL(logoBlob);
		} else {
			logoSrc = Config.getRestUrl("oe", `tenants/${this.ctx.session.sessionInfo.tenant.id}/banner`);
			const svgHeader = TenantSvgHeader.replace("{tenant}", this.ctx.session.sessionInfo.tenant.name);
			const logoBlob = new Blob([svgHeader], { type: 'image/svg+xml' });
			logoSrc = URL.createObjectURL(logoBlob);
		}
		//const logoFile = new File([blob], "logo.svg");
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
				<Profile />
			</GlobalHeader>
		);
	}

}
