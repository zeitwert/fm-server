
import { Spinner } from "@salesforce/design-system-react";
import { session } from "@zeitwert/ui-model";
import { observer } from "mobx-react";
import React, { Suspense } from "react";
import ErrorBoundary from "../ErrorBoundary";
import { RouteComponentProps, withRouter } from "./withRouter";

// home, tenant, user, account, contact, portfolio, building, task
const ComponentPathInfo = {
	"home": "home/ui/HomeArea",
	"tenant": "tenant/ui/TenantArea",
	"user": "user/ui/UserArea",
	"account": "account/ui/AccountArea",
	"contact": "contact/ui/ContactArea",
	"portfolio": "portfolio/ui/PortfolioArea",
	"building": "building/ui/BuildingArea",
	"task": "task/ui/TaskArea",
}

@observer
class DynamicView extends React.Component<RouteComponentProps> {

	render() {
		const componentPath = this.componentName(this.props.params.path!)!;
		if (!componentPath) {
			return <div><strong>{`DynamicView.render(${this.props.params.path}): ${componentPath}`}</strong></div>;
		}
		try {
			const AreaComponent = React.lazy(() =>
				import(`../../areas/${componentPath}` /* webpackChunkName: "[request]" */)
			);
			return (
				<ErrorBoundary>
					<Suspense fallback={<Spinner variant="brand" size="large" />}>
						<AreaComponent />
					</Suspense>
				</ErrorBoundary>
			);
		} catch (e) {
			return <div><strong>{`DynamicView.render(${this.props.params.path}): ${componentPath}`}</strong></div>;
		}
	}

	private componentName(path: string) {
		if (session.appInfo) {
			const areaPath = path ?? session.appInfo?.defaultArea ?? "/";
			return ComponentPathInfo[areaPath];
			//return session.appInfo!.areas.find((a) => a.path === areaPath)?.component;
		}
	}

}

export default withRouter(DynamicView);
