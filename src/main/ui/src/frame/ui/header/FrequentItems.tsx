import Button from "@salesforce/design-system-react/components/button";
import Icon from "@salesforce/design-system-react/components/icon";
import MediaObject from "@salesforce/design-system-react/components/media-object";
import Popover from "@salesforce/design-system-react/components/popover";
import { GLOBAL_HEADER_FAVORITES } from "@salesforce/design-system-react/utilities/constants";
import { Aggregate, AppStore, UserInfo } from "@zeitwert/ui-model";
import classNames from "classnames";
import { computed, makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";
import { Link } from "react-router-dom";

interface FrequentItemsProps {
	store: AppStore;
	user: UserInfo;
}

@observer
export default class FrequentItems extends React.Component<FrequentItemsProps> {
	static displayName = GLOBAL_HEADER_FAVORITES;

	@observable isPopoverOpen = false;

	@computed get frequentItems() {
		return [];
		//return this.props.store.frequentItems;
		// .slice()
		// .sort((fi1, fi2) => (fi1.meta!.touches < fi2.meta!.touches ? 1 : -1));
	}

	constructor(props: FrequentItemsProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		//await this.props.store.loadFrequentItems(this.props.user);
	}

	render() {
		const buttonClasses = classNames("slds-button_icon slds-global-actions__favorites-action");
		return (
			<div className="slds-global-actions__favorites slds-dropdown-trigger slds-dropdown-trigger_click">
				<Popover
					heading="Frequent"
					align="top right"
					body={this.renderItems(this.frequentItems || [])}
					isOpen={this.isPopoverOpen}
					onClick={() => (this.isPopoverOpen = !this.isPopoverOpen)}
					onRequestClose={() => (this.isPopoverOpen = false)}
				>
					<Button
						className={buttonClasses}
						iconCategory="utility"
						iconName="edit"
						iconSize="small"
						iconVariant="border"
						title="Frequent"
						variant="icon"
					/>
				</Popover>
			</div>
		);
	}

	renderItems(items: Aggregate[]) {
		return (
			<>
				{!items.length && <p>No frequent items.</p>}
				{items.map((item) => (
					<MediaObject
						key={"frequent-" + item.id}
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
									{item.owner.caption} ⋅ {/*item.meta!.touches*/} touches
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
