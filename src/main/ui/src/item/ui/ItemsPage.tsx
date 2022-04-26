
import { Button } from "@salesforce/design-system-react";
import { AggregateStore, EntityType, EntityTypeInfo, EntityTypes, ItemList, ItemListModel } from "@zeitwert/ui-model";
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
import { AppCtx } from "../../App";
import ItemModal from "./ItemModal";
import ItemPanel from "./ItemPanel";
import ItemListController from "./list/ItemListController";

interface ItemsPageProps extends RouteComponentProps {
	entityType: EntityType;
	store: AggregateStore;
	listDatamart: string;
	listTemplate: string;
	canCreate?: boolean;
	createFormId?: string;
	actionButtons?: React.ReactNode[];
	createEditor?: () => JSX.Element;
	onAfterCreate?: (store: AggregateStore) => void;
}

@inject("session", "showAlert", "showToast")
@observer
class ItemsPage extends React.Component<ItemsPageProps> {

	@observable listStore: ItemList;
	@observable store: AggregateStore;
	@observable showPanel: boolean = false;
	@observable panelItem: any | undefined = undefined;
	type: EntityTypeInfo;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: ItemsPageProps) {
		super(props);
		makeObservable(this);
		this.listStore = ItemListModel.create({ datamart: props.listDatamart });
		this.store = this.props.store;
		this.type = EntityTypes[this.props.entityType];
	}

	render() {
		const { entityType, createFormId, createEditor, listTemplate, actionButtons, canCreate } = this.props;
		return (
			<>
				<ItemListController
					label={this.type.label}
					iconCategory={this.type.iconCategory}
					iconName={this.type.iconName}
					defaultTemplate={listTemplate}
					store={this.listStore}
					reportTemplates={{
						date: DateDataTableCell,
						link: DataTableCellWithLink,
						documentIcon: DataTableCellWithDocumentIcon,
						entityIcon: DataTableCellWithEntityIcon
					}}
					actionButtons={
						(actionButtons || []).concat(
							canCreate
								? [<Button key="new" label={"New " + this.type.labelSingular} onClick={this.openEditor} />]
								: []
						)
					}
					onClick={this.openPanel}
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
				{
					this.showPanel && (
						<ItemPanel item={this.panelItem} onClose={this.closePanel} />
					)
				}
			</>
		);
	}

	private openPanel = (item: any) => {
		this.showPanel = true;
		this.panelItem = item;
	}

	private closePanel = () => {
		this.showPanel = false;
	}

	private openEditor = () => {
		this.store!.create({
			owner: this.ctx.session.sessionInfo!.user
		});
		this.props.onAfterCreate && this.props.onAfterCreate(this.store);
	};

	private cancelEditor = async () => {
		await this.store!.cancel();
	};

	private closeEditor = async () => {
		try {
			await this.store!.store();
			this.ctx.showToast("success", `New ${this.type.labelSingular} ${this.store!.id} created`);
			this.props.navigate("/" + this.store.typeName + "/" + this.store!.id);
		} catch (error: any) {
			this.ctx.showAlert(
				"error",
				`Could not create new ${this.type.labelSingular}: ` +
				(error.detail ? error.detail : error.title ? error.title : error)
			);
		}
	};

}

export default withRouter(ItemsPage);
