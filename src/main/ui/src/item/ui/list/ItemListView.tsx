
import { Spinner } from "@salesforce/design-system-react";
import { Template } from "@zeitwert/ui-model";
import { observer } from "mobx-react";
import React from "react";
import ItemListContent from "./ItemListContent";
import ItemListHeader, { ItemListHeaderProps } from "./ItemListHeader";

interface ItemListProps extends ItemListHeaderProps {
	template?: Template;
	dataTableCellTemplates?: any;
	isLoading: boolean;
	onClick?: (itemId: string) => void;
}

@observer
export default class ItemListView extends React.Component<ItemListProps> {

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
					onRefresh={this.props.onRefresh}
					onSelectTemplate={this.props.onSelectTemplate}
				/>
				{
					this.props.isLoading &&
					<Spinner variant="brand" size="large" />
				}
				{
					!this.props.isLoading &&
					<ItemListContent
						template={this.props.template}
						reportData={this.props.reportData}
						dataTableCellTemplates={this.props.dataTableCellTemplates}
						onClick={this.props.onClick}
					/>
				}
			</>
		);
	}

}
