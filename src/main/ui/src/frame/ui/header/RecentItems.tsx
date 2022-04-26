import { Button, Icon, MediaObject, Popover } from "@salesforce/design-system-react";
import { GLOBAL_HEADER_FAVORITES } from "@salesforce/design-system-react/utilities/constants";
import { Aggregate, AppStore, DateFormat, UserInfo } from "@zeitwert/ui-model";
import classNames from "classnames";
import { computed, makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";
import { Link } from "react-router-dom";

interface RecentItemsProps {
	store: AppStore;
	user: UserInfo;
}

@observer
export default class RecentItems extends React.Component<RecentItemsProps> {

	static displayName = GLOBAL_HEADER_FAVORITES;

	@observable isPopoverOpen = false;

	@computed get recentItems() {
		return [];
		// return this.props.store.recentItems
		// 	.slice()
		// 	.sort((ri1, ri2) =>
		// 		new Date(ri1.meta!.modifiedAt).getTime() < new Date(ri2.meta!.modifiedAt).getTime() ? 1 : -1
		// 	);
	}

	constructor(props: RecentItemsProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		//await this.props.store.loadRecentItems(this.props.user);
	}

	render() {
		const buttonClasses = classNames("slds-button_icon slds-global-actions__favorites-action");
		return (
			<div className="slds-global-actions__favorites slds-dropdown-trigger slds-dropdown-trigger_click">
				<Popover
					heading="Recent"
					align="top right"
					body={this.renderItems(this.recentItems || [])}
					isOpen={this.isPopoverOpen}
					onClick={() => (this.isPopoverOpen = !this.isPopoverOpen)}
					onRequestClose={() => (this.isPopoverOpen = false)}
				>
					<Button
						className={buttonClasses}
						iconCategory="utility"
						iconName="clock"
						iconSize="small"
						iconVariant="border"
						title="Recent"
						variant="icon"
					/>
				</Popover>
			</div>
		);
	}

	renderItems(items: Aggregate[]) {
		return (
			<>
				{!items.length && <p>No recent items.</p>}
				{items.map((item) => (
					<MediaObject
						key={"recent-" + item.id}
						body={
							<>
								<div className="slds-text-body_small">
									<Link
										to={"/" + item.type.type + "/" + item.id}
										onClick={() => (this.isPopoverOpen = false)}
									>
										{item.caption}
									</Link>
								</div>
								<div className="slds-text-body_small">
									{item.owner.caption} ⋅ {DateFormat.relativeTime(new Date(), item.meta!.modifiedAt)}
								</div>
							</>
						}
						figure={<Icon category={item.type.iconCategory} name={item.type.iconName} size="small" />}
						verticalCenter
					/>
				))}
			</>
		);
	}
}
