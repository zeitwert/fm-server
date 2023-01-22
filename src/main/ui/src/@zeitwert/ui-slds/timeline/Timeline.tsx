
import { Button } from "@salesforce/design-system-react";
import classNames from "classnames";
import { action, makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";

interface TimelineProps {
	children: React.ReactNode;
}

export class Timeline extends React.Component<TimelineProps> {
	render() {
		return <ul className="slds-timeline">{this.props.children}</ul>;
	}
}

interface TimelineItemProps {
	name: string;
	type: string;
	icon: any;
	date: string;
	body: JSX.Element | string;
	detail: React.ReactNode;
	isExpandable?: boolean;
	isExpanded?: boolean;
	onClick?: (e: any) => void;
	onToggle?: (expanded: boolean) => void;
}

@observer
export class TimelineItem extends React.Component<TimelineItemProps> {

	@observable isExpanded = false;

	constructor(props: TimelineItemProps) {
		super(props);
		makeObservable(this);
	}

	componentDidMount() {
		this.isExpanded = this.props.isExpanded || false;
	}

	render() {
		const { name, type, icon, date, body, isExpandable, onClick, onToggle } = this.props;
		const classes = classNames(
			{
				"slds-timeline__item_expandable": isExpandable,
				"slds-is-open": this.isExpanded,
			},
			"slds-timeline__item_" + type,
		);
		return (
			<li>
				<div className={classes}>
					<div className="slds-media">
						<div className="slds-media__figure">
							<Button
								assistiveText={{ icon: "Toggle details for " + type }}
								iconCategory="utility"
								iconName="switch"
								iconSize="x-small"
								iconVariant="bare"
								onClick={action(() => {
									isExpandable && (this.isExpanded = !this.isExpanded);
									onToggle && onToggle(this.isExpanded);
								})}
								variant="icon"
								style={
									!this.isExpanded
										? { transform: "rotate(-90deg)", marginRight: "0.125rem", color: isExpandable ? "" : "transparent" }
										: { marginRight: "0.125rem" }
								}
							/>
							{icon}
						</div>
						<div className="slds-media__body">
							<div className="slds-grid slds-grid_align-spread">
								<div className="slds-grid slds-grid_vertical-align-center slds-truncate_container_75 slds-no-space">
									<h3 className="slds-truncate" title={name}>
										{
											onClick && (
												<a href="/#" onClick={(e: any) => onClick(e)}>
													<strong>{name}</strong>
												</a>
											)
										}
										{
											!onClick && <strong>{name}</strong>
										}
									</h3>
								</div>
								<div className="slds-timeline__actions slds-timeline__actions_inline">
									<p className="slds-timeline__date">{date}</p>
								</div>
							</div>
							<p>{body}</p>
							{this.isExpanded && this.renderDetail()}
						</div>
					</div>
				</div>
			</li>
		);
	}

	private renderDetail() {
		const { detail } = this.props;
		return (
			<article
				className="slds-box slds-timeline__item_details slds-theme_shade slds-m-top_x-small slds-m-horizontal_xx-small slds-p-around_medium"
				id="timeline-item-expanded"
				aria-hidden="false"
			>
				{detail}
			</article>
		);
	}
}
