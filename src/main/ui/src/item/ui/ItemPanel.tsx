
import { observer } from "mobx-react";
import React from "react";

export interface ItemPanelProps {
	item: any;
	onClose: () => void;
}

const ItemPanel: React.FC<ItemPanelProps> = observer((props) => {
	const { item } = props;
	let { name, /*link,*/ accountName, address } = item;
	let city: string;
	let zipMatch = /\d{4}/.exec(address);
	if (zipMatch) {
		city = address.substring(zipMatch.index);
		address = address.substring(0, zipMatch.index - 1);
	}
	return (
		<section aria-labelledby="panel-heading-id" className="fa-item-panel slds-popover slds-popover_panel slds-popover_prompt slds-popover_prompt_top-right slds-popover_prompt_bottom-right" role="dialog">
			<div>
				<button className="slds-button slds-button_icon slds-button_icon slds-button_icon-small slds-float_right slds-popover__close" title="Close dialog" onClick={props.onClose}>
					<svg className="slds-button__icon" aria-hidden="true">
						<use xlinkHref="/assets/icons/utility-sprite/svg/symbols.svg#close"></use>
					</svg>
					<span className="slds-assistive-text">Close</span>
				</button>
			</div>
			<div className="slds-popover__header">
				<header className="slds-media slds-media_center slds-m-bottom_small">
					<span className="slds-icon_container slds-icon-standard-account slds-media__figure">
						<svg className="slds-icon slds-icon_small" aria-hidden="true">
							<use xlinkHref="/assets/icons/standard-sprite/svg/symbols.svg#store"></use>
						</svg>
					</span>
					<div className="slds-media__body">
						<h2 className="slds-text-heading_medium slds-hyphenate" id="panel-heading-id">
							<a href="/#">{name}</a>
						</h2>
					</div>
				</header>
				<footer className="slds-grid slds-wrap slds-grid_pull-padded">
					<div className="slds-p-horizontal_small slds-size_1-of-2 slds-p-bottom_x-small">
						<dl>
							<dt>
								<p className="slds-popover_panel__label slds-truncate" title="Billing Address">Address</p>
							</dt>
							<dd>
								<p className="slds-truncate" title="3500 Deer Creek Rd.">{address}</p>
								<p className="slds-truncate" title="Palo Alto, CA 94304">{city!}</p>
							</dd>
						</dl>
					</div>
					<div className="slds-p-horizontal_small slds-size_1-of-2 slds-p-bottom_x-small">
						<dl>
							<dt>
								<p className="slds-popover_panel__label slds-truncate" title="Account">Account</p>
							</dt>
							<dd>
								<a href="/#">{accountName}</a>
							</dd>
						</dl>
					</div>
					<div className="slds-p-horizontal_small slds-size_1-of-2 slds-p-bottom_x-small">
						<dl>
							<dt>
								<p className="slds-popover_panel__label slds-truncate" title="Website">Website</p>
							</dt>
							<dd>
								<a href="/#">www.zeitwert.io</a>
							</dd>
						</dl>
					</div>
					<div className="slds-p-horizontal_small slds-size_1-of-2 slds-p-bottom_x-small">
						<dl>
							<dt>
								<p className="slds-popover_panel__label slds-truncate" title="Account Owner">Account Owner</p>
							</dt>
							<dd>
								<a href="/#">Jeff Maguire</a>
							</dd>
						</dl>
					</div>
				</footer>
			</div>
			<div>
				<img
					className="slds-align_absolute-center"
					style={{ width: "100%", maxHeight: "200px" }}
					src={"/demo/building-" + (Math.round(10 * Math.random()) % 10) + ".jpeg"}
					alt={name}
				/>
			</div>
			<div className="slds-popover__body">
				<dl className="slds-popover__body-list">
					<dt className="slds-m-bottom_small">
						<div className="slds-media slds-media_center">
							<div className="slds-media__figure">
								<span className="slds-icon_container slds-icon-standard-opportunity">
									<svg className="slds-icon slds-icon_small" aria-hidden="true">
										<use xlinkHref="/assets/icons/standard-sprite/svg/symbols.svg#opportunity"></use>
									</svg>
									<span className="slds-assistive-text">Opportunities</span>
								</span>
							</div>
							<div className="slds-media__body">
								<p className="slds-text-heading_small slds-hyphenate">Opportunities (2+)</p>
							</div>
						</div>
					</dt>
					<dd className="slds-m-top_x-small">
						<p className="slds-truncate" title="Tesla - Mule ESB">
							<a href="/#">Tesla - Mule ESB</a>
						</p>
						<dl className="slds-list_horizontal slds-wrap slds-text-body_small">
							<dt className="slds-item_label slds-text-color_weak slds-truncate" title="Value">Value</dt>
							<dd className="slds-item_detail slds-text-color_weak slds-truncate" title="$500,000">$500,000</dd>
							<dt className="slds-item_label slds-text-color_weak slds-truncate" title="Close Date">Close Date</dt>
							<dd className="slds-item_detail slds-text-color_weak slds-truncate" title="Dec 15, 2015">Dec 15, 2015</dd>
						</dl>
					</dd>
					<dd className="slds-m-top_x-small">
						<p className="slds-truncate" title="Tesla - Anypoint Studios">
							<a href="/#">Tesla - Anypoint Studios</a>
						</p>
						<dl className="slds-list_horizontal slds-wrap slds-text-body_small">
							<dt className="slds-item_label slds-text-color_weak slds-truncate" title="Value">Value</dt>
							<dd className="slds-item_detail slds-text-color_weak slds-truncate" title="$60,000">$60,000</dd>
							<dt className="slds-item_label slds-text-color_weak slds-truncate" title="Close Date">Close Date</dt>
							<dd className="slds-item_detail slds-text-color_weak slds-truncate" title="Jan 15, 2016">Jan 15, 2016</dd>
						</dl>
					</dd>
					<dd className="slds-m-top_x-small slds-text-align_right">
						<a href="/#" title="View all Opportunities">View All</a>
					</dd>
				</dl>
				<dl className="slds-popover__body-list">
					<dt className="slds-m-bottom_small">
						<div className="slds-media slds-media_center">
							<div className="slds-media__figure">
								<span className="slds-icon_container slds-icon-standard-case">
									<svg className="slds-icon slds-icon_small" aria-hidden="true">
										<use xlinkHref="/assets/icons/standard-sprite/svg/symbols.svg#case"></use>
									</svg>
									<span className="slds-assistive-text">Cases</span>
								</span>
							</div>
							<div className="slds-media__body">
								<p className="slds-text-heading_small slds-hyphenate">Cases (1)</p>
							</div>
						</div>
					</dt>
					<dd className="slds-m-top_x-small">
						<p className="slds-truncate" title="Tesla - Anypoint Studios">
							<a href="/#">Tesla - Anypoint Studios</a>
						</p>
						<dl className="slds-list_horizontal slds-wrap slds-text-body_small">
							<dt className="slds-item_label slds-text-color_weak slds-truncate" title="Value">Value</dt>
							<dd className="slds-item_detail slds-text-color_weak slds-truncate" title="$60,000">$60,000</dd>
							<dt className="slds-item_label slds-text-color_weak slds-truncate" title="Close Date">Close Date</dt>
							<dd className="slds-item_detail slds-text-color_weak slds-truncate" title="Jan 15, 2016">Jan 15, 2016</dd>
						</dl>
					</dd>
					<dd className="slds-m-top_x-small slds-text-align_right">
						<a href="/#" title="View all Opportunities">View All</a>
					</dd>
				</dl>
			</div>
		</section>
	);
});

export default ItemPanel;
