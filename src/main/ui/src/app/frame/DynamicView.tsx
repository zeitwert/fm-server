
import { Spinner } from "@salesforce/design-system-react";
import { AppCtx } from "app/App";
import { inject, observer } from "mobx-react";
import React, { Suspense } from "react";
import ErrorBoundary from "../ErrorBoundary";
import { RouteComponentProps, withRouter } from "./withRouter";

@inject("logger", "session")
@observer
class DynamicView extends React.Component<RouteComponentProps> {

	get ctx() {
		return this.props as any as AppCtx;
	}

	render() {
		const componentPath = this.componentName(this.props.params.path!)!;
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
			return <div><strong>{`DynamicView.render(${this.props.params.path}: ${componentPath}`}</strong></div>;
		}
	}

	private componentName(path: string) {
		if (this.ctx.session.appInfo) {
			const areaPath = path ?? this.ctx.session.appInfo?.defaultArea ?? "/";
			return this.ctx.session.appInfo!.areas.find((a) => a.path === areaPath)?.component;
		}
	}

}

export default withRouter(DynamicView);
