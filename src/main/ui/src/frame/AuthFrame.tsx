
import { AppCtx } from "App";
import { inject, observer } from "mobx-react";
import React from "react";
import LoginForm from "session/ui/LoginForm";
import { RouteComponentProps, withRouter } from "./app/withRouter";

@inject("logger", "session", "showAlert")
@observer
class AuthFrame extends React.Component<RouteComponentProps> {

	get ctx() {
		return this.props as any as AppCtx;
	}

	async componentDidMount() {
		const session = this.ctx.session;
		if (session.isAuthenticated && !session.isInit) {
			try {
				await this.ctx.session.initSession();
			} catch (error: any) {
				this.ctx.showAlert("error", error.toString());
			}
		}
	}

	render() {
		const session = this.ctx.session;
		return (
			<div className="fa-flip-card">
				<div className={"fa-flip-card-inner" + (session.isAuthenticated ? " fa-is-authenticated" : "")}>
					<div className="fa-flip-card-front" style={{ zIndex: -1 }}>
						<LoginForm session={session} />
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
