import { AppLauncher, AppLauncherExpandableSection, AppLauncherTile, Avatar, GlobalNavigationBar, GlobalNavigationBarDropdown, GlobalNavigationBarLink, GlobalNavigationBarRegion } from "@salesforce/design-system-react";
import { ApplicationArea, MenuItem } from "@zeitwert/ui-model";
import { AppCtx } from "App";
import { RouteComponentProps, withRouter } from "frame/app/withRouter";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

@inject("logger", "navigator", "session")
@observer
class AppNavigation extends React.Component<RouteComponentProps> {
	@observable isLauncherOpen = false;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: RouteComponentProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		if (!this.ctx.session.appInfo) {
			return null;
		}
		return (
			<GlobalNavigationBar>
				<GlobalNavigationBarRegion region="primary">
					<AppLauncher
						id="app-launcher-trigger"
						triggerName={this.ctx.session.appInfo!.name}
						triggerOnClick={() => (this.isLauncherOpen = true)}
						isOpen={this.isLauncherOpen}
						onClose={() => (this.isLauncherOpen = false)}
					>
						<AppLauncherExpandableSection title="Applications" nonCollapsible>
							{this.ctx.session.appList!.map((app: any) => (
								<AppLauncherTile
									key={app.id}
									title={app.name}
									description={app.description}
									iconNode={
										<Avatar
											variant="entity"
											imgSrc={`/assets/images/app/${app.icon}.png`}
											imgAlt={app.name}
											size="large"
										/>
									}
									onClick={() => this.onAppClick(app.id)}
								/>
							))}
						</AppLauncherExpandableSection>
					</AppLauncher>
				</GlobalNavigationBarRegion>
				<GlobalNavigationBarRegion region="secondary" navigation>
					{
						this.ctx.session.appInfo!.areas.map((area: ApplicationArea) => {
							if (area.menuAction) {
								return (
									<GlobalNavigationBarLink
										active={this.isActive(area.path, this.ctx.session.appInfo?.defaultArea === area.path)}
										label={area.name}
										id={area.id}
										key={area.id}
										onClick={() =>
											this.props.navigate(
												this.ctx.navigator.navigate(area.id, area.menuAction!.navigation)
											)
										}
									/>
								);
							}
							return (
								<GlobalNavigationBarDropdown
									assistiveText={{
										icon: "Open menu item submenu"
									}}
									id={area.id}
									options={
										area.menu!.items.map((item: MenuItem) => {
											switch (item._type) {
												case "zeitwert.app.domain.MenuHeader":
													return {
														label: item.name,
														value: item.id,
														type: "header"
													};
												case "zeitwert.app.domain.MenuAction":
													return {
														label: item.name,
														value: item.id,
														iconCategory: item.icon.split(":")[0],
														iconName: item.icon.split(":")[1],
														href: "/#",
														onClick: () =>
															this.props.navigate(
																this.ctx.navigator.navigate(area.id, item.navigation)
															)
													};
												default:
													return null;
											}
										})
									}
								/>
							);
						})
					}
				</GlobalNavigationBarRegion>
			</GlobalNavigationBar>
		);
	}

	private onAppClick = async (appId: string) => {
		await this.ctx.session.setApp(appId);
		const area = this.ctx.session.appInfo!.areas.find((app) => app.id === this.ctx.session.appInfo!.defaultArea)!;
		this.props.navigate(this.ctx.navigator.navigate(area.id, area.menuAction!.navigation));
		this.isLauncherOpen = false;
	};

	private isActive(areaPath: string, isDefault: boolean) {
		const path = this.props.location.pathname;
		if (!path) {
			return false;
		} else if (path === "/") {
			return isDefault;
		}
		return path.startsWith("/" + areaPath);
	}

}

export default withRouter(AppNavigation);
