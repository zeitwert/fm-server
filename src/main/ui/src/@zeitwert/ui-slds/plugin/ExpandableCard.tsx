import { Button, Card, MediaObject } from "@salesforce/design-system-react";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";

interface ExpandableCardProps {
	icon: React.ReactElement;
	className?: string;
	expandedClassName?: string;
	body: JSX.Element | JSX.Element[];
	expandedBody: JSX.Element | JSX.Element[];
	onExpand?: () => void;
}

@observer
export default class ExpandableCard extends React.Component<ExpandableCardProps> {
	@observable expanded = false;

	constructor(props: ExpandableCardProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const { className, expandedClassName, icon, body, expandedBody, onExpand } = this.props;
		const classes = ["slds-m-around_none"];
		if (className) {
			classes.push(className);
		}
		const expandedClasses = ["slds-card__body"];
		if (expandedClassName) {
			expandedClasses.push(expandedClassName);
		}
		return (
			<>
				<Card
					heading=""
					className={classes.join(" ")}
					bodyClassName="slds-m-arond_none"
					header={
						<MediaObject
							figure={
								<>
									<Button
										iconCategory="utility"
										iconName={this.expanded ? "chevrondown" : "chevronright"}
										iconSize="small"
										iconVariant="bare"
										onClick={() => {
											onExpand && onExpand();
											this.expanded = !this.expanded;
										}}
										variant="icon"
									/>
									{icon}
								</>
							}
							body={body}
							verticalCenter
							canTruncate
						/>
					}
				>
					{this.expanded && <div className={expandedClasses.join(" ")}>{expandedBody}</div>}
				</Card>
			</>
		);
	}
}
