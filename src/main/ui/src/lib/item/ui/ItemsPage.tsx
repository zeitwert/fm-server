
import { Button, ButtonGroup } from "@salesforce/design-system-react";
import { AggregateStore, EntityType, EntityTypes, ItemList, ItemListModel, session } from "@zeitwert/ui-model";
import { DataTableCellWithDocumentIcon, DataTableCellWithEntityIcon, DataTableCellWithLink, DateDataTableCell } from "@zeitwert/ui-slds";
import { RouteComponentProps, withRouter } from "app/frame/withRouter";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";
import ItemModal from "./ItemModal";
import { getNewEntityText } from "./ItemUtils";
import ItemListController from "./list/ItemListController";

interface ItemsPageProps extends RouteComponentProps {
	entityType: EntityType;
	store: AggregateStore;
	listDatamart: string;
	listTemplate: string;
	canCreate: boolean;
	customActions?: JSX.Element;
	createEditor?: () => JSX.Element;
	onAfterCreate?: (store: AggregateStore) => void;
	onOpenPreview?: (itemId: string) => void;
	onSelectionChange?: (selectedItems: any[]) => void;
}

@observer
class ItemsPage extends React.Component<ItemsPageProps> {

	@observable listStore: ItemList;
	@observable sortProperty?: string;
	@observable sortDirection?: "asc" | "desc" | undefined;

	constructor(props: ItemsPageProps) {
		super(props);
		makeObservable(this);
		this.listStore = ItemListModel.create({ datamart: props.listDatamart });
		this.sortProperty = session.userPrefs.getUserPref(this.props.entityType, "sortProperty");
		this.sortDirection = session.userPrefs.getUserPref(this.props.entityType, "sortDirection");
	}

	render() {
		const { entityType, createEditor, listTemplate, customActions, canCreate } = this.props;
		const type = EntityTypes[this.props.entityType];
		const newText = getNewEntityText(type);
		return (
			<>
				<ItemListController
					label={type.label}
					iconCategory={type.iconCategory}
					iconName={type.iconName}
					defaultTemplate={listTemplate}
					store={this.listStore}
					dataTableCellTemplates={{
						date: DateDataTableCell,
						link: DataTableCellWithLink,
						documentIcon: DataTableCellWithDocumentIcon,
						entityIcon: DataTableCellWithEntityIcon
					}}
					actionButtons={
						<>
							{customActions}
							{
								canCreate &&
								<ButtonGroup variant="list">
									<Button key="new" label={newText} onClick={this.openEditor} />
								</ButtonGroup>
							}
						</>
					}
					sortProperty={this.sortProperty}
					sortDirection={this.sortDirection}
					onClick={this.openPreview}
					onSort={this.storeSort}
					onSelectionChange={this.props.onSelectionChange}
				/>
				{
					this.props.store?.isInTrx && (
						<ItemModal
							store={this.props.store}
							entityType={entityType}
						>
							{createEditor}
						</ItemModal>
					)
				}
			</>
		);
	}

	private openPreview = (itemId: string) => {
		const type = EntityTypes[this.props.entityType];
		type.hasPreview && this.props.onOpenPreview?.(itemId);
	}

	private storeSort = (property: string, direction: "asc" | "desc" | undefined): void => {
		session.userPrefs.setUserPref(this.props.entityType, "sortProperty", property);
		session.userPrefs.setUserPref(this.props.entityType, "sortDirection", direction);
		this.sortProperty = property;
		this.sortDirection = direction;
	}

	private openEditor = () => {
		this.props.store!.create({
			owner: session.sessionInfo!.user
		});
		this.props.onAfterCreate && this.props.onAfterCreate(this.props.store);
	};

}

export default withRouter(ItemsPage);
