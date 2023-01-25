import { ItemList } from "@zeitwert/ui-model";
import { AppCtx } from "app/App";
import ItemListView from "lib/item/ui/list/ItemListView";
import { inject, observer } from "mobx-react";
import React from "react";

interface ItemListControllerProps {
	label: string;
	iconCategory: string;
	iconName: string;
	defaultTemplate: string;
	store: ItemList;
	dataTableCellTemplates?: any;
	actionButtons: JSX.Element;
	sortProperty?: string;
	sortDirection?: "asc" | "desc" | undefined;
	onClick?: (item: any) => void;
	onSort?: (property: string, direction: "asc" | "desc" | undefined) => void;
	onSelectionChange?: (selectedItems: any[]) => void;
}

@inject("showToast")
@observer
export default class ItemListController extends React.Component<ItemListControllerProps> {

	get ctx() {
		return this.props as any as AppCtx;
	}

	componentDidMount() {
		const { store, defaultTemplate } = this.props;
		store.initTemplates();
		store.executeTemplate(defaultTemplate).catch((error) => {
			console.error("Could not load item list", error);
			this.ctx.showToast("error", "Could not load item list");
		});
	}

	render() {
		const { store } = this.props;
		return (
			<ItemListView
				label={this.props.label}
				iconCategory={this.props.iconCategory}
				iconName={this.props.iconName}
				templateList={store.templateList}
				defaultTemplateId={this.props.defaultTemplate}
				modifiedAt={store.modifiedAt}
				template={store.template}
				reportData={store.reportData}
				sortProperty={this.props.sortProperty}
				sortDirection={this.props.sortDirection}
				dataTableCellTemplates={this.props.dataTableCellTemplates}
				isLoading={store.isLoading}
				actionButtons={this.props.actionButtons}
				onRefresh={() => store.executeTemplate()}
				onSelectTemplate={(templateId) => store.executeTemplate(templateId)}
				onClick={this.props.onClick}
				onSort={this.props.onSort}
				onSelectionChange={this.props.onSelectionChange}
			/>
		);
	}

}
