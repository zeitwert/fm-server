
import { KANBAN_API, LayoutType, Template } from "@zeitwert/ui-model";
import { ReportViewer } from "@zeitwert/ui-slds";
import { AppCtx } from "app/App";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

interface ItemListContentProps {
	template?: Template;
	reportData: any;
	sortProperty?: string;
	sortDirection?: "asc" | "desc" | undefined;
	dataTableCellTemplates?: any;
	onClick?: (itemId: string) => void;
	onSort?: (property: string, direction: "asc" | "desc" | undefined) => void;
	onSelectionChange?: (selectedItems: any[]) => void;
}

@inject("showToast")
@observer
export default class ItemListContent extends React.Component<ItemListContentProps> {

	@observable
	private reportData: any = {};

	constructor(props: ItemListContentProps) {
		super(props);
		this.reportData = Object.assign({}, this.props.reportData);
		makeObservable(this);
	}

	get ctx() {
		return this.props as any as AppCtx;
	}

	render() {
		const layout = this.props.template?.layout;
		if (!layout) {
			return <></>;
		}
		let options = {};
		switch (layout.layoutType) {
			default:
			case LayoutType.Line:
				break;
			case LayoutType.Kanban:
				options = {
					onCardMoved: this.onCardMoved,
					cardActions: [
						{
							name: "Edit",
							action: (itemId: string) => Promise.resolve(console.log("Edit card " + itemId))
						},
						{
							name: "Delete",
							action: (itemId: string) => Promise.resolve(console.log("Delete card " + itemId))
						}
					]
				};

				break;
		}
		return (
			<ReportViewer
				layout={layout}
				data={this.reportData}
				dataTableCellTemplates={this.props.dataTableCellTemplates}
				sortProperty={this.props.sortProperty}
				sortDirection={this.props.sortDirection}
				options={options}
				onClick={this.props.onClick}
				onSort={this.props.onSort}
				onSelectionChange={this.props.onSelectionChange}
			/>
		);
	}

	private onCardMoved = async (itemId: string, id: string) => {
		const layout = this.props.template!.layout!;
		const { docType, groupBy, modifyUrl } = layout.layout;
		const field = groupBy.serverFieldName;
		try {
			await KANBAN_API.updateItem(modifyUrl, docType, itemId, field, id);
			this.ctx.showToast("success", docType.charAt(0).toUpperCase() + docType.slice(1) + " stored.");
		} catch (err) {
			this.ctx.showToast("warning", "Error while modifying " + docType + "!");
		}
	};

}
