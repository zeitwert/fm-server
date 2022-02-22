import { Col, Grid, Row } from "@zeitwert/ui-slds/common/Grid";
import { AppCtx } from "App";
import { inject, observer } from "mobx-react";
import React from "react";
import HomeCardCondition from "./HomeCardCondition";
import HomeCardRenovations from "./HomeCardRecentActivityList";
import HomeCardRenovationList from "./HomeCardRenovationList";
import HomeCardStatistics from "./HomeCardStatistics";
import HomeCardTodoList from "./HomeCardTodoList";

@inject("appStore", "showToast")
@observer
export default class HomePage extends React.Component {

	get ctx() {
		return this.props as any as AppCtx;
	}

	componentDidMount() {
		const store = this.ctx.appStore;
		if (!!store.msg) {
			this.ctx.showToast(store.msg?.status, store.msg?.text);
		}
	}

	render() {
		return (
			<Grid className="fa-height-100">
				<Row cols={3} nowrap className="fa-height-50">
					<Col totalCols={3} cols={1} className="slds-p-around_xx-small">
						<HomeCardRenovationList />
					</Col>
					<Col totalCols={3} cols={1} className="slds-p-around_xx-small">
						<HomeCardStatistics />
					</Col>
					<Col totalCols={3} cols={1} className="slds-p-around_xx-small">
						<HomeCardTodoList />
					</Col>
				</Row>
				<Row cols={3} nowrap className="fa-height-50">
					<Col totalCols={3} cols={2} className="slds-p-around_xx-small">
						<HomeCardCondition />
					</Col>
					<Col totalCols={3} cols={1} className="slds-p-around_xx-small">
						<HomeCardRenovations />
					</Col>
				</Row>
			</Grid>
		);
	}

}
