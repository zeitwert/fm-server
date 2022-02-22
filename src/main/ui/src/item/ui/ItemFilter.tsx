
import { Enumerated } from "@zeitwert/ui-model";
import _ from "lodash";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";
import Select from "react-select";

interface ItemFilterProps {
	children: (filteredItems: any[]) => any;
	availableItems: any[];
	selectedType?: Enumerated;
	selectedAreas?: Enumerated[];
	areasOptions: any[];
}

@observer
export class ItemFilter extends React.Component<ItemFilterProps> {

	@observable searchText: string;
	@observable searchType?: any;
	@observable searchAreas: any[];

	constructor(props: ItemFilterProps) {
		super(props);
		makeObservable(this);
		this.searchText = "";
		this.searchType = this.props.selectedType
			? {
				value: this.props.selectedType.id,
				label: this.props.selectedType.name
			}
			: undefined;
		this.searchAreas = (this.props.selectedAreas || []).map((item) => ({
			value: item.id,
			label: item.name
		}));
	}

	/**
	 * Filter available items using the search filter.
	 *
	 * @param {any[]} availableItems
	 * @returns {any[]}
	 * @private
	 */
	private filter(availableItems: any[]) {
		let filteredItems = availableItems;
		// Filter by areas.
		if (this.searchAreas && this.searchAreas.length) {
			filteredItems = filteredItems.filter(
				(item) =>
					item.areas.length === 0 ||
					_.intersectionWith(
						item.areas,
						this.searchAreas,
						(item1: any, item2: any) => item1.id === item2.value
					).length > 0
			);
		}
		// Filter by search text.
		if (this.searchText.length) {
			filteredItems = filteredItems.filter((item) =>
				item.caption.toLowerCase().includes(this.searchText.toLowerCase())
			);
		}
		return filteredItems;
	}

	render() {
		const { areasOptions, availableItems } = this.props;
		return (
			<>
				<div className="slds-grid slds-grid_vertical-align-center slds-gutters_direct-x-small slds-m-around_medium">
					<div className="slds-col" style={{ maxWidth: "8%" }}>
						<span className="fa-subtitle">Search:</span>
					</div>
					<div className="slds-col" style={{ width: "18%" }}>
						<input
							className="fa__text__input fa__component__input"
							placeholder="Search..."
							onChange={(event: any) => (this.searchText = event.target.value)}
							value={this.searchText}
						/>
					</div>
					<div className="slds-col" style={{ maxWidth: "8%" }}>
						<span className="fa-subtitle">Areas:</span>
					</div>
					<div className="slds-col" style={{ width: "35%" }}>
						<Select
							className="fa__select"
							classNamePrefix="fa__select"
							placeholder="Areas"
							value={this.searchAreas}
							onChange={(selectedOption: any) => (this.searchAreas = selectedOption)}
							options={areasOptions}
							isMulti
							isSearchable
						/>
					</div>
				</div>
				{this.props.children(this.filter(availableItems))}
			</>
		);
	}
}
