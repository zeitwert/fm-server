
import { Spinner } from "@salesforce/design-system-react";
import { session } from "@zeitwert/ui-model";
import { AppCtx } from "app/App";
import { inject, observer } from "mobx-react";
import React, { Suspense } from "react";
import ErrorBoundary from "../ErrorBoundary";
import { RouteComponentProps, withRouter } from "./withRouter";

@inject("logger")
@observer
class DynamicView extends React.Component<RouteComponentProps> {

	get ctx() {
		return this.props as any as AppCtx;
	}

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
			return session.appInfo!.areas.find((a) => a.path === areaPath)?.component;
		}
	}

}

export default withRouter(DynamicView);
