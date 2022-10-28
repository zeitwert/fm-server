
import AccountArea from "account/ui/AccountArea";
import BuildingArea from "building/ui/BuildingArea";
import BuildingReportArea from "building/ui/BuildingReportArea";
import ContactArea from "contact/ui/ContactArea";
import DocumentArea from "dms/ui/DocumentArea";
import { AppCtx } from "frame/App";
import HomeArea from "home/ui/HomeArea";
import LeadArea from "lead/ui/LeadArea";
import { inject, observer } from "mobx-react";
import PortfolioArea from "portfolio/ui/PortfolioArea";
import React from "react";
import TaskArea from "task/ui/TaskArea";
import TenantArea from "tenant/ui/TenantArea";
import UserArea from "user/ui/UserArea";
import ErrorBoundary from "../ErrorBoundary";
import { RouteComponentProps, withRouter } from "./withRouter";

const AreaMap: {
	[id: string]: React.FunctionComponent | React.ComponentClass;
} = {
	AccountArea: AccountArea,
	BuildingArea: BuildingArea,
	BuildingReportArea: BuildingReportArea,
	ContactArea: ContactArea,
	DocumentArea: DocumentArea,
	HomeArea: HomeArea,
	LeadArea: LeadArea,
	PortfolioArea: PortfolioArea,
	TaskArea: TaskArea,
	TenantArea: TenantArea,
	UserArea: UserArea
};

@inject("logger", "session")
@observer
class DynamicView extends React.Component<RouteComponentProps> {

	get ctx() {
		return this.props as any as AppCtx;
	}

	render() {
		const componentName = this.componentName(this.props.params.path!)!;
		const DynamicComponent = DynamicView.component(componentName);
		if (DynamicComponent) {
			return (
				<ErrorBoundary>
					<DynamicComponent />
				</ErrorBoundary>
			);
		} else if (componentName) {
			alert("Could not instantiate component " + componentName);
		}
		return null;
	}

	private componentName(path: string) {
		if (this.ctx.session.appInfo) {
			const areaPath = (path ? path : this.ctx.session.appInfo?.defaultArea) || "/";
			const area = this.ctx.session.appInfo!.areas.find((a) => a.path === areaPath);
			return area ? area.component : undefined;
		}
	}

	private static component(componentName: string): React.FunctionComponent | React.ComponentClass {
		return AreaMap[componentName] ? AreaMap[componentName] : (UnderConstruction as any);
	}
}

export default withRouter(DynamicView);

class UnderConstruction extends React.Component {
	render() {
		return <div />;
	}
}
