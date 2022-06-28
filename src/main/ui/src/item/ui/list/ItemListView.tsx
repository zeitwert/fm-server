import { session, Template } from "@zeitwert/ui-model";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";
import ItemListContent from "./ItemListContent";
import ItemListHeader, { ItemListHeaderProps } from "./ItemListHeader";

interface ItemListProps extends ItemListHeaderProps {
	template?: Template;
	reportTemplates?: any;
	onClick?: (itemId: string) => void;
}

@observer
export default class ItemListView extends React.Component<ItemListProps> {

	@observable
	hasMap: boolean = false; // @TODO proper

	@observable
	showMap: boolean = false; // @TODO proper

	constructor(props: ItemListProps) {
		super(props);
		makeObservable(this);
		//this.hasMap = this.props.defaultTemplateId?.indexOf("buildings")! >= 0;
	}

	render() {
		return (
			<>
				<ItemListHeader
					label={this.props.label}
					iconCategory={this.props.iconCategory}
					iconName={this.props.iconName}
					templateList={this.props.templateList}
					defaultTemplateId={this.props.defaultTemplateId}
					modifiedAt={this.props.modifiedAt}
					reportData={this.props.reportData}
					actionButtons={this.props.actionButtons}
					hasMap={this.hasMap}
					showMap={this.showMap}
					onRefresh={this.props.onRefresh}
					onSelectTemplate={this.props.onSelectTemplate}
					onShowMap={(showMap: boolean) => { this.showMap = !this.showMap; }}
				/>
				{
					!session.isNetworkActive &&
					<ItemListContent
						template={this.props.template}
						reportData={this.props.reportData}
						reportTemplates={this.props.reportTemplates}
						hasMap={this.hasMap}
						showMap={this.showMap}
						onClick={this.props.onClick}
					/>
				}
			</>
		);
	}

}
