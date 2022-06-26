
import { Spinner } from "@salesforce/design-system-react";
import { BuildingStore, BuildingStoreModel, Currency, session } from "@zeitwert/ui-model";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";

export interface BuildingPreviewProps {
	buildingId: string;
	onClose: () => void;
}

@observer
export default class BuildingPreview extends React.Component<BuildingPreviewProps> {

	@observable buildingStore: BuildingStore = BuildingStoreModel.create({});
	@observable isLoaded = false;

	constructor(props: BuildingPreviewProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		this.isLoaded = false;
		await this.buildingStore.load(this.props.buildingId);
		this.isLoaded = true;
	}

	async componentDidUpdate(prevProps: BuildingPreviewProps) {
		if (this.props.buildingId !== prevProps.buildingId) {
			this.isLoaded = false;
			await this.buildingStore.load(this.props.buildingId!);
			this.isLoaded = true;
		}
	}

	render() {
		if (!this.isLoaded) {
			return <Spinner variant="brand" size="large" />;
		}
		const building = this.buildingStore.building!;
		const { name, street: address, city, zip } = building;
		const accountName = building.account?.caption;
		return (
			<>
				<div>
					<button className="slds-button slds-button_icon slds-button_icon slds-button_icon-small slds-float_right slds-popover__close" title="Close dialog" onClick={this.props.onClose}>
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
								<a href={`/building/${building.id}`}>{name}</a>
							</h2>
						</div>
					</header>
					<footer className="slds-grid slds-wrap slds-grid_pull-padded">
						<div className="slds-p-horizontal_small slds-p-vertical_small slds-align_absolute-center">
							<img
								className="slds-align_absolute-center"
								style={{ maxWidth: "100%", maxHeight: "300px" }}
								src={building.coverFotoUrl}
								alt={name}
							/>
						</div>
						<div className="slds-p-horizontal_small slds-size_1-of-2 slds-p-bottom_x-small">
							<dl>
								<dt>
									<p className="slds-popover_panel__label slds-truncate">Addresse</p>
								</dt>
								<dd>
									<p className="slds-truncate">{address}</p>
									<p className="slds-truncate">{zip} {city}</p>
								</dd>
							</dl>
						</div>
						<div className="slds-p-horizontal_small slds-size_1-of-2 slds-p-bottom_x-small">
							<dl>
								<dt>
									<p className="slds-popover_panel__label slds-truncate">Kunde</p>
								</dt>
								<dd>
									<a href={`/account/${building.account?.id}`}>{accountName}</a>
								</dd>
							</dl>
						</div>
						<div className="slds-p-horizontal_small slds-size_1-of-2 slds-p-bottom_x-small">
							<dl>
								<dt>
									<p className="slds-popover_panel__label slds-truncate">Gebäudekategorie</p>
								</dt>
								<dd>
									<p className="slds-truncate">{building.partCatalog?.name}</p>
								</dd>
							</dl>
						</div>
						<div className="slds-p-horizontal_small slds-size_1-of-2 slds-p-bottom_x-small">
							<dl>
								<dt>
									<p className="slds-popover_panel__label slds-truncate">Unterhaltsplan</p>
								</dt>
								<dd>
									<p className="slds-truncate">{building.maintenanceStrategy?.name}</p>
								</dd>
							</dl>
						</div>
						<div className="slds-p-horizontal_small slds-size_1-of-2 slds-p-bottom_x-small">
							<dl>
								<dt>
									<p className="slds-popover_panel__label slds-truncate">Baujahr</p>
								</dt>
								<dd>
									<p className="slds-truncate">{building.buildingYear}</p>
								</dd>
							</dl>
						</div>
						<div className="slds-p-horizontal_small slds-size_1-of-2 slds-p-bottom_x-small">
							<dl>
								<dt>
									<p className="slds-popover_panel__label slds-truncate">Versichert {building.insuredValueYear ? ` (${building.insuredValueYear})` : ""}</p>
								</dt>
								<dd>
									<p className="slds-truncate">{session.formatter.formatAmount(1000 * building.insuredValue!, Currency.CHF)}</p>
								</dd>
							</dl>
						</div>
					</footer>
				</div>
				{
					false &&
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
				}
			</>
		);
	}

}