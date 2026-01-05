import { AppLauncher, AppLauncherExpandableSection, AppLauncherTile, Avatar, GlobalNavigationBar, GlobalNavigationBarLink, GlobalNavigationBarRegion } from "@salesforce/design-system-react";
import { ApplicationArea, session } from "@zeitwert/ui-model";
import { RouteComponentProps, withRouter } from "app/frame/withRouter";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";

@observer
class AppNavigation extends React.Component<RouteComponentProps> {
	@observable isLauncherOpen = false;

	constructor(props: RouteComponentProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		if (!session.appInfo) {
			return null;
		}
		return (
			<GlobalNavigationBar className="slds-context-bar_tabs">
				<GlobalNavigationBarRegion region="primary" className="slds-context-bar__item_tab">
					<AppLauncher
						id="app-launcher-trigger"
						triggerName={session.appInfo!.name}
						triggerOnClick={() => (this.isLauncherOpen = true)}
						isOpen={this.isLauncherOpen}
						onClose={() => (this.isLauncherOpen = false)}
					>
						<AppLauncherExpandableSection title="Applications" nonCollapsible>
							{
								session.appList!.map((app: any) => (
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
								))
							}
						</AppLauncherExpandableSection>
					</AppLauncher>
				</GlobalNavigationBarRegion>
				<GlobalNavigationBarRegion region="secondary" navigation>
					{
						session.appInfo!.areas.map((area: ApplicationArea) => {
							const isDefault = session.appInfo?.defaultArea === area.path;
							return (
								<GlobalNavigationBarLink
									active={this.isActive(area.path, isDefault)}
									label={area.name}
									id={area.id}
									key={area.id}
									className="slds-context-bar__item_tab"
									onClick={() => (!isDefault || !this.isActive(area.path, false)) && this.navigateToArea(area)}
								/>
							);
						})
					}
				</GlobalNavigationBarRegion>
			</GlobalNavigationBar>
		);
	}

	private navigateToArea(area: ApplicationArea) {
		const targetUrl = area.path.startsWith("/") ? area.path : "/" + area.path;
		this.props.navigate(targetUrl);
	}

	private onAppClick = async (appId: string) => {
		await session.setApp(appId);
		const area = session.appInfo!.areas.find((a) => a.id === session.appInfo!.defaultArea)!;
		this.navigateToArea(area);
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
