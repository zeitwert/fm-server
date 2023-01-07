
import { Card } from "@salesforce/design-system-react";
import { DateFormat } from "@zeitwert/ui-model";
import { AppCtx } from "app/App";
import { RouteComponentProps, withRouter } from "app/frame/withRouter";
import { inject, observer } from "mobx-react";
import React from "react";

interface HomeMyDayProps extends RouteComponentProps {
	className?: string;
}

@inject("session", "showToast")
@observer
class HomeMyDay extends React.Component<HomeMyDayProps> {

	get ctx() {
		return this.props as any as AppCtx;
	}

	render() {
		return (
			<Card
				heading=""
				header={
					<div className="slds-size_full slds-text-align_center">
						<div className="slds-text-heading_medium">
							<strong>My Day</strong>
						</div>
						<div className="slds-text-heading_medium">
							<strong>{DateFormat.longer(new Date(), false)}</strong>
						</div>
					</div>
				}
				className="fa-height-100"
				bodyClassName="slds-card__body_inner my-day-card-body-inner"
			>
			</Card>
		);
	}
}

export default withRouter(HomeMyDay);
