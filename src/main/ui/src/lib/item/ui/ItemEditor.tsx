import { AggregateStore, EntityType, EntityTypes } from "@zeitwert/ui-model";
import { AppCtx } from "app/App";
import { action } from "mobx";
import { inject, observer } from "mobx-react";
import React, { PropsWithChildren } from "react";
import { ItemEditorButtons } from "./ItemEditorButtons";

interface ItemEditorProps {
	store: AggregateStore;
	entityType: EntityType;
	showEditButtons: boolean;
	customButtons?: JSX.Element;
}

@inject("showAlert", "showToast")
@observer
export default class ItemEditor extends React.Component<PropsWithChildren<ItemEditorProps>> {

	get ctx() {
		return this.props as any as AppCtx;
	}

	render() {
		const {
			store,
			showEditButtons,
			customButtons,
			children,
		} = this.props;
		const buttons = (
			<ItemEditorButtons
				showEditButtons={showEditButtons}
				doEdit={store.isInTrx}
				allowStore={true/* TODO */}
				onOpenEditor={action(() => this.onOpen())}
				onCancelEditor={action(() => this.onCancel())}
				onCloseEditor={action(() => this.onClose())}
				customButtons={customButtons}
			/>
		);
		return (
			<>
				<div className="slds-m-horizontal_medium slds-text-align_right" style={{ position: "absolute", right: "0", top: "3px" }}				>
					{buttons}
				</div>
				{children}
			</>
		);
	}

	private onOpen = () => {
		this.props.store.edit();
	};

	private onCancel = async () => {
		this.props.store.cancel();
	};

	private onClose = async () => {
		const { entityType, store } = this.props;
		const entityTypeInfo = EntityTypes[entityType];
		const id = store.id!;
		try {
			await store.store();
			this.ctx.showToast("success", `${entityTypeInfo.labelSingular} gespeichert`);
		} catch (error: any) {
			// eslint-disable-next-line
			if (error.status == 409) { // version conflict
				await store.load(id);
			}
			this.ctx.showAlert(
				"error",
				`Konnte ${entityTypeInfo.labelSingular} nicht speichern: ` +
				(error.detail ? error.detail : error.title ? error.title : error)
			);
		}
	};

}
