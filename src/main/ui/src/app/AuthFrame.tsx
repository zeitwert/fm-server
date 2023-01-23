
import { session } from "@zeitwert/ui-model";
import { inject, observer } from "mobx-react";
import React, { PropsWithChildren } from "react";
import { AppCtx } from "./App";
import { RouteComponentProps, withRouter } from "./frame/withRouter";
import LoginForm from "./LoginForm";

@inject("logger", "showAlert")
@observer
class AuthFrame extends React.Component<PropsWithChildren<RouteComponentProps>> {

	get ctx() {
		return this.props as any as AppCtx;
	}

	async componentDidMount() {
		if (session.isAuthenticated && !session.isInit) {
			try {
				await session.initSession();
			} catch (error: any) {
				this.ctx.showAlert("error", error.toString());
			}
		}
	}

	render() {
		if (!session.isAuthenticated && this.props.location.pathname !== "/") {
			window.location.href = "/";
		}
		return (
			<div className="fa-flip-card">
				<div className={"fa-flip-card-inner" + (session.isAuthenticated ? " fa-is-authenticated" : "")}>
					<div className="fa-flip-card-front" style={{ zIndex: -1 }}>
						{
							session.doShowLoginForm &&
							<LoginForm session={session} />
						}
					</div>
					<div className="fa-flip-card-back">
						{this.props.children}
					</div>
				</div>
			</div>
		);
	}

}

export default withRouter(AuthFrame);
