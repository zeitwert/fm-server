
import { GlobalHeader, GlobalHeaderSearch } from "@salesforce/design-system-react";
import { Config } from "@zeitwert/ui-model";
import { Canvg, presets } from "canvg";
import { AppCtx } from "frame/App";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import AppNavigation from "./AppNavigation";
import Help from "./header/Help";
import Profile from "./header/Profile";
import SearchBar from "./header/SearchBar";
import Setup from "./header/Setup";
import { SvgHeader as AccountSvgHeader } from "./SvgAccountHeader";
import { SvgHeader as TenantSvgHeader } from "./SvgTenantHeader";

const preset = presets.offscreen();

@inject("appStore", "logger", "session")
@observer
export default class AppHeader extends React.Component {

	@observable svgHeader: string | undefined;
	@observable logoSrc: string | undefined;

	constructor(props: any) {
		super(props);
		makeObservable(this);
		this.calcHeader();
	}

	get ctx() {
		return this.props as any as AppCtx;
	}

	render() {
		if (!this.ctx.session.sessionInfo) {
			return <></>;
		}
		if (!this.svgHeader) {
			this.calcHeader();
		}
		//const logoFile = new File([blob], "logo.svg");
		return (
			<GlobalHeader
				logoSrc={this.logoSrc || ""}
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

	private calcHeader = (): void => {
		if (!this.ctx.session.sessionInfo) {
			return;
		}
		if (!this.ctx.session.appInfo?.id) {
			this.svgHeader = "";
		} else if (this.ctx.session.sessionInfo.account) {
			this.svgHeader = AccountSvgHeader
				.replace("{logo}", Config.getRestUrl("account", `accounts/${this.ctx.session.sessionInfo.account.id}/logo`))
				.replace("{account}", this.ctx.session.sessionInfo.account.name)
				.replace("{tenant}", this.ctx.session.sessionInfo.tenant.name);
			this.toPng({ width: 300, height: 50, svg: this.svgHeader });
			//this.logoSrc = await this.toPng({ width: 300, height: 50, svg: svgHeader });
			// const logoBlob = new Blob([svgHeader], { type: 'image/svg+xml' });
			// logoSrc = URL.createObjectURL(logoBlob);
			// logoSrc = Config.getRestUrl("account", `accounts/${this.ctx.session.sessionInfo.account.id}/banner`);
		} else {
			this.svgHeader = TenantSvgHeader
				.replace("{logo}", Config.getRestUrl("oe", `tenants/${this.ctx.session.sessionInfo.tenant.id}/logo`))
				.replace("{tenant}", this.ctx.session.sessionInfo.tenant.name);
			this.toPng({ width: 300, height: 50, svg: this.svgHeader });
			//logoSrc = this.toPng({ width: 300, height: 50, svg: svgHeader });
			// const logoBlob = new Blob([svgHeader], { type: 'image/svg+xml' });
			// logoSrc = URL.createObjectURL(logoBlob);
			// logoSrc = Config.getRestUrl("oe", `tenants/${this.ctx.session.sessionInfo.tenant.id}/banner`);
		}
	}

	private toPng = async (data: any): Promise<void> => {
		const { width, height, svg } = data;
		const canvas = new OffscreenCanvas(width, height);
		const ctx = canvas.getContext("2d")!;
		const v = await Canvg.from(ctx, svg, preset);
		await v.render(); // render only first frame, ignoring animations and mouse.
		const blob = await canvas.convertToBlob();
		this.logoSrc = URL.createObjectURL(blob);
	}

}
