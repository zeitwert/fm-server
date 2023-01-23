import { session } from "@zeitwert/ui-model";
import { Col, Grid, Row } from "@zeitwert/ui-slds";
import { AppCtx } from "app/App";
import { RouteComponentProps, withRouter } from "app/frame/withRouter";
import SidePanel from "app/ui/SidePanel";
import BuildingPreview from "areas/building/ui/BuildingPreview";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import HomeCardMap from "./HomeCardMap";
import HomeCardOpenActivities from "./HomeCardOpenActivities";
import HomeCardOverview from "./HomeCardOverview";
import HomeCardRecentActions from "./HomeCardRecentActions";
import HomeCardStatistics from "./HomeCardStatistics";

@inject("appStore", "showToast")
@observer
class HomePage extends React.Component<RouteComponentProps> {

	@observable showPreview = false;
	@observable previewItemId: string | undefined;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: RouteComponentProps) {
		super(props);
		makeObservable(this);
	}

	componentDidMount(): void {
		session.setHelpContext("dashboard");
	}

	componentDidUpdate(prevProps: RouteComponentProps) {
		// close preview on route change
		if (this.props.location !== prevProps.location) {
			this.showPreview = false;
			this.previewItemId = undefined;
		}
	}

	render() {
		return (
			<>
				<Grid className="fa-height-100" isVertical={false}>
					<Row cols={2} nowrap className="fa-height-100">
						<Col totalCols={12} cols={6}>
							<Row cols={1} nowrap className="fa-height-100">
								<Col totalCols={1} cols={1} className="slds-p-around_xx-small">
									<HomeCardMap onClick={this.openPreview} />
								</Col>
							</Row>
						</Col>
						<Col totalCols={12} cols={6}>
							<Row cols={2} nowrap className="fa-height-50">
								<Col totalCols={2} cols={1} className="slds-p-around_xx-small">
									<HomeCardOpenActivities />
								</Col>
								<Col totalCols={2} cols={1} className="slds-p-around_xx-small">
									<HomeCardOverview />
								</Col>
							</Row>
							<Row cols={2} nowrap className="fa-height-50">
								<Col totalCols={2} cols={1} className="slds-p-around_xx-small">
									<HomeCardRecentActions />
								</Col>
								<Col totalCols={2} cols={1} className="slds-p-around_xx-small">
									<HomeCardStatistics />
								</Col>
							</Row>
						</Col>
					</Row>
				</Grid>
				{
					this.showPreview && this.previewItemId &&
					<SidePanel>
						<BuildingPreview buildingId={this.previewItemId} onClose={this.closePreview} />
					</SidePanel>
				}
			</>
		);
	}

	private openPreview = (itemId: string) => {
		if (itemId === this.previewItemId) {
			this.showPreview = false;
			this.previewItemId = undefined;
		} else {
			this.showPreview = true;
			this.previewItemId = itemId;
		}
	};

	private closePreview = () => {
		this.showPreview = false;
		this.previewItemId = undefined;
	};

}

export default withRouter(HomePage);
