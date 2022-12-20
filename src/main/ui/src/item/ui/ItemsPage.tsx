
import { Button } from "@salesforce/design-system-react";
import { AggregateStore, EntityType, EntityTypes, ItemList, ItemListModel, session } from "@zeitwert/ui-model";
import {
	DataTableCellWithDocumentIcon,
	DataTableCellWithEntityIcon,
	DataTableCellWithLink,
	DateDataTableCell
} from "@zeitwert/ui-slds/custom/CustomDataTableCells";
import { RouteComponentProps, withRouter } from "frame/app/withRouter";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import { AppCtx } from "../../frame/App";
import ItemModal from "./ItemModal";
import { getNewEntityText } from "./ItemUtils";
import ItemListController from "./list/ItemListController";

interface ItemsPageProps extends RouteComponentProps {
	entityType: EntityType;
	store: AggregateStore;
	listDatamart: string;
	listTemplate: string;
	canCreate: boolean;
	createFormId?: string;
	actionButtons?: React.ReactNode[];
	createEditor?: () => JSX.Element;
	onAfterCreate?: (store: AggregateStore) => void;
	onOpenPreview?: (itemId: string) => void;
	onSelectionChange?: (selectedItems: any[]) => void;
}

@inject("session", "showAlert", "showToast")
@observer
class ItemsPage extends React.Component<ItemsPageProps> {

	@observable listStore: ItemList;
	@observable store: AggregateStore;
	@observable sortProperty?: string;
	@observable sortDirection?: "asc" | "desc" | undefined;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: ItemsPageProps) {
		super(props);
		makeObservable(this);
		this.listStore = ItemListModel.create({ datamart: props.listDatamart });
		this.store = this.props.store;
		this.sortProperty = session.userPrefs.getUserPref(this.props.entityType, "sortProperty");
		this.sortDirection = session.userPrefs.getUserPref(this.props.entityType, "sortDirection");
	}

	render() {
		const { entityType, createFormId, createEditor, listTemplate, actionButtons, canCreate } = this.props;
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
						(actionButtons || []).concat(
							canCreate
								? [<Button key="new" label={newText} onClick={this.openEditor} />]
								: []
						)
					}
					sortProperty={this.sortProperty}
					sortDirection={this.sortDirection}
					onClick={this.openPreview}
					onSort={this.storeSort}
					onSelectionChange={this.props.onSelectionChange}
				/>
				{
					this.store?.isInTrx && (
						<ItemModal
							store={this.store}
							entityType={entityType}
							formId={createFormId}
							itemAlias={entityType}
							onClose={this.closeEditor}
							onCancel={this.cancelEditor}
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
		this.store!.create({
			owner: this.ctx.session.sessionInfo!.user
		});
		this.props.onAfterCreate && this.props.onAfterCreate(this.store);
	};

	private cancelEditor = async () => {
		this.store!.cancel();
	};

	private closeEditor = async () => {
		const type = EntityTypes[this.props.entityType];
		try {
			await this.store!.store();
			this.ctx.showToast("success", `${type.labelSingular} gespeichert`);
			this.props.navigate("/" + this.store.typeName + "/" + this.store!.id);
		} catch (error: any) {
			this.ctx.showAlert(
				"error",
				`Could not create new ${type.labelSingular}: ` +
				(error.detail ? error.detail : error.title ? error.title : error)
			);
		}
	};

}

export default withRouter(ItemsPage);
