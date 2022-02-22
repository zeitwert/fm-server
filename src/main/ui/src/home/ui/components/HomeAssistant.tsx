import { deepFind } from "@comunas/ui-model";
import { Grid } from "@comunas/ui-slds/common/Grid";
import Button from "@salesforce/design-system-react/components/button";
import ButtonGroup from "@salesforce/design-system-react/components/button-group";
import Card from "@salesforce/design-system-react/components/card";
import Icon from "@salesforce/design-system-react/components/icon";
import Tooltip from "@salesforce/design-system-react/components/tooltip";
import { AppCtx } from "App";
import { inject, observer } from "mobx-react";
import React from "react";
import { Link } from "react-router-dom";
import { ModalCall, ModalTask } from "./HomeAssistantActionModals";

@inject("appStore", "session")
@observer
export default class HomeAssistant extends React.Component {
	get ctx() {
		return this.props as any as AppCtx;
	}

	async componentDidMount() {
		await this.ctx.appStore.getLeads(this.ctx.session.sessionInfo!.user?.email);
	}

	render() {
		return (
			<Card heading="Assistant" className="fa-height-100" bodyClassName="slds-m-around_none">
				{this.ctx.appStore.leads.length <= 0 && (
					<p className="slds-m-horizontal_small">Relax. You have qualified all tasks.</p>
				)}
				{this.ctx.appStore.leads.map((lead: any, i: any) => (
					<HomeAssistantCard key={"hac:" + i} lead={lead} />
				))}
			</Card>
		);
	}
}

interface HomeAssistantCardProps {
	lead: any;
}

interface HomeAssistantCardState {
	expanded: boolean;
	modalCallOpen: boolean;
	modalTaskOpen: boolean;
	modalConversionOpen: boolean;
}

@inject("appStore", "showAlert", "showToast")
class HomeAssistantCard extends React.Component<HomeAssistantCardProps, HomeAssistantCardState> {
	constructor(props: HomeAssistantCardProps) {
		super(props);

		this.state = {
			expanded: false,
			modalCallOpen: false,
			modalTaskOpen: false,
			modalConversionOpen: false
		};
	}

	get ctx() {
		return this.props as any as AppCtx;
	}

	render() {
		const { lead } = this.props;
		const { expanded, modalCallOpen, modalTaskOpen, modalConversionOpen } = this.state;

		return (
			<>
				<article className="slds-card slds-m-top_none">
					<div className="slds-card__header slds-grid slds-p-around_x-small slds-m-around_xx-small">
						<header className="slds-media slds-media_center slds-has-flexi-truncate">
							<Button
								iconCategory="utility"
								iconName={expanded ? "chevrondown" : "chevronright"}
								iconSize="small"
								iconVariant="bare"
								onClick={() => this.setState({ expanded: !expanded })}
								variant="icon"
							/>
							<div className="slds-media__figure">
								<Icon
									category={lead.leadTrigger.icon.split(":")[0]}
									name={lead.leadTrigger.icon.split(":")[1]}
									size="small"
								/>
							</div>
							<div className="slds-media__body">
								<h2 className="title">
									<span className="slds-text-heading_small">
										{lead.descriptionShort || lead.leadTrigger.name}
									</span>
								</h2>
								<Link to={"/account/" + deepFind(lead, "advisee.id")}>
									{deepFind(lead, "account.advisee.name")}
								</Link>{" "}
								({lead.opportunityArea.join(", ")})
							</div>
						</header>

						{expanded ? (
							<Tooltip content="Discard / Postpone" position="overflowBoundaryElement">
								<Button
									iconCategory="action"
									iconName="close"
									iconVariant="border"
									iconSize="large"
									onClick={() => this.ctx.appStore.removeLead(lead.id)}
								/>
							</Tooltip>
						) : (
							<ButtonGroup>
								<Tooltip content="Log a Call" position="overflowBoundaryElement">
									<Button
										iconCategory="action"
										iconName="log_a_call"
										iconVariant="border"
										iconSize="large"
										onClick={() =>
											this.setState({
												modalCallOpen: !modalCallOpen
											})
										}
									/>
								</Tooltip>
								<Tooltip content="Create a Task" position="overflowBoundaryElement">
									<Button
										iconCategory="action"
										iconName="new_task"
										iconVariant="border"
										iconSize="large"
										onClick={() =>
											this.setState({
												modalTaskOpen: !modalTaskOpen
											})
										}
									/>
								</Tooltip>
								<Tooltip content="Convert" position="overflowBoundaryElement">
									<Button
										iconCategory="action"
										iconName="new_opportunity"
										iconVariant="border"
										iconSize="large"
										onClick={() =>
											this.setState({
												modalConversionOpen: !modalConversionOpen
											})
										}
									/>
								</Tooltip>
								<Tooltip
									content="Discard / Postpone"
									position="overflowBoundaryElement"
									align="top right"
								>
									<Button
										iconCategory="action"
										iconName="close"
										iconVariant="border"
										iconSize="large"
										onClick={() => this.ctx.appStore.removeLead(lead.id)}
									/>
								</Tooltip>
							</ButtonGroup>
						)}
					</div>
					{expanded && (
						<div
							className="slds-card__body"
							style={{
								paddingLeft: "2rem",
								paddingRight: "1rem"
							}}
						>
							<div>
								<p>{deepFind(lead, "descriptionLong")}</p>
								<div className="slds-text-color_weak">Source: {deepFind(lead, "source")}</div>
							</div>
							<Grid isVertical={false} style={{ marginTop: "0.25rem" }}>
								<div className="slds-size_1-of-3" style={{ padding: "0.125rem" }}>
									<Button
										className="slds-size_1-of-1"
										variant="neutral"
										label="Log a Call"
										onClick={() =>
											this.setState({
												modalCallOpen: !modalCallOpen
											})
										}
									/>
								</div>
								<div className="slds-size_1-of-3" style={{ padding: "0.125rem" }}>
									<Button
										className="slds-size_1-of-1"
										variant="neutral"
										label="Create a Task"
										onClick={() =>
											this.setState({
												modalTaskOpen: !modalTaskOpen
											})
										}
									/>
								</div>
								<div className="slds-size_1-of-3" style={{ padding: "0.125rem" }}>
									<Button
										className="slds-size_1-of-1"
										variant="neutral"
										label="Convert"
										onClick={() =>
											this.setState({
												modalConversionOpen: !modalConversionOpen
											})
										}
									/>
								</div>
							</Grid>
						</div>
					)}
				</article>
				{modalCallOpen && (
					<ModalCall
						isOpen={modalCallOpen}
						close={() => this.setState({ modalCallOpen: false })}
						save={() => this.ctx.appStore.convertLead(lead.id, "meeting")}
						saveAndConvert={() => this.setState({ modalConversionOpen: true })}
						convertEnabled
						entity={lead}
					/>
				)}
				{modalTaskOpen && (
					<ModalTask
						isOpen={modalTaskOpen}
						close={() => this.setState({ modalTaskOpen: false })}
						save={() => this.ctx.appStore.convertLead(lead.id, "meeting")}
						entity={lead}
					/>
				)}
			</>
		);
	}

}
