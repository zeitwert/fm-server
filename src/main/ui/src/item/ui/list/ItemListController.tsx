import { ItemList } from "@zeitwert/ui-model";
import { AppCtx } from "App";
import ItemListView from "item/ui/list/ItemListView";
import { inject, observer } from "mobx-react";
import React from "react";

interface ItemListControllerProps {
	label: string;
	iconCategory: string;
	iconName: string;
	defaultTemplate: string;
	store: ItemList;
	reportTemplates?: any;
	actionButtons: React.ReactNode[];
	onClick?: (item: any) => void;
}

@inject("logger", "showToast")
@observer
export default class ItemListController extends React.Component<ItemListControllerProps> {

	get ctx() {
		return this.props as any as AppCtx;
	}

	componentDidMount() {
		const { store, defaultTemplate } = this.props;
		store.initTemplates();
		store.executeTemplate(defaultTemplate).catch((error) => {
			this.ctx.logger.error("Could not load item list", error);
			this.ctx.showToast("warning", "Could not load item list");
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
				reportTemplates={this.props.reportTemplates}
				isLoading={store.isLoading}
				actionButtons={this.props.actionButtons}
				onRefresh={() => store.executeTemplate()}
				onSelectTemplate={(templateId) => store.executeTemplate(templateId)}
				onClick={this.props.onClick}
			/>
		);
	}

}
