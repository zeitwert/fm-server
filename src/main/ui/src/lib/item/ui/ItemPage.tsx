
import { Card } from "@salesforce/design-system-react";
import { Col, Grid } from "@zeitwert/ui-slds";
import classNames from "classnames";
import React from "react";

interface ItemGridProps {
	children?: JSX.Element[] | JSX.Element;
}

export class ItemGrid extends React.Component<ItemGridProps> {
	render() {
		return (
			<Grid
				isVertical={false}
				className="slds-is-relative slds-wrap slds-grow slds-gutters_direct-x-small slds-m-top_small"
				style={{ height: "1px" }}
			>
				{this.props.children}
			</Grid>
		);
	}
}

interface ItemLeftPartProps {
	hasItemPath?: boolean;
	isFullWidth?: boolean;
	children?: JSX.Element;
}

export class ItemLeftPart extends React.Component<ItemLeftPartProps> {
	render() {
		const { hasItemPath, isFullWidth } = this.props;
		const classes = classNames(
			"slds-size_1-of-1",
			isFullWidth ? "" : "slds-medium-size_1-of-2 slds-large-size_2-of-3 slds-x-large-size_3-of-4",
			hasItemPath ? "fa-item-part" : "fa-height-100"
		);
		return (
			<Col className={classes}>
				<Card heading="" hasNoHeader className="fa-height-100" bodyClassName="slds-m-around_none">
					{this.props.children}
				</Card>
			</Col>
		);
	}
}

interface ItemRightPartProps {
	hasItemPath?: boolean;
	isFullWidth?: boolean;
	children?: JSX.Element;
}

export class ItemRightPart extends React.Component<ItemRightPartProps> {
	render() {
		const { hasItemPath, isFullWidth } = this.props;
		if (isFullWidth) {
			return null;
		}
		const classes = classNames(
			"slds-size_1-of-1 slds-medium-size_1-of-2 slds-large-size_1-of-3 slds-x-large-size_1-of-4",
			hasItemPath ? "fa-item-part" : "fa-height-100"
		);
		return (
			<Col className={classes}>
				<Card heading="" hasNoHeader className="fa-height-100" bodyClassName="slds-m-around_none">
					{this.props.children}
				</Card>
			</Col>
		);
	}
}
