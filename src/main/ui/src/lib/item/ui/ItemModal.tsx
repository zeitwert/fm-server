
import { Icon, MediaObject, Modal } from "@salesforce/design-system-react";
import { AggregateStore, EntityType, EntityTypes } from "@zeitwert/ui-model";
import { AppCtx } from "app/App";
import { RouteComponentProps, withRouter } from "app/frame/withRouter";
import { inject } from "mobx-react";
import React from "react";
import { ItemEditorButtons } from "./ItemEditorButtons";
import { getEditEntityText, getNewEntityText } from "./ItemUtils";

interface ItemModalProps extends RouteComponentProps {
	store: AggregateStore;
	entityType: EntityType;
	customButtons?: JSX.Element;
	size?: "small" | "medium" | "large";
	children?: () => JSX.Element;
}

@inject("showAlert", "showToast")
class ItemModal extends React.Component<ItemModalProps> {

	get ctx() {
		return this.props as any as AppCtx;
	}

	render() {
		const entityType = EntityTypes[this.props.entityType];
		const { iconCategory, iconName } = entityType;
		const { store, children } = this.props;
		const headerText = store.isNew ? getNewEntityText(entityType) : getEditEntityText(entityType);
		const heading = (
			<MediaObject
				body={<>{headerText}</>}
				figure={<Icon category={iconCategory as any} name={iconName as any} size="small" />}
				verticalCenter
			/>
		);
		const buttons = (
			<ItemEditorButtons
				showEditButtons={true}
				doEdit={store.isInTrx}
				allowStore={true/*!this.isFormDisabled*/}
				onCancelEditor={this.onCancel}
				onCloseEditor={this.onClose}
			/>
		);
		return (
			<Modal
				heading={heading}
				onRequestClose={this.onCancel}
				dismissOnClickOutside={false}
				footer={buttons}
				size={this.props.size || "small"}
				isOpen
			>
				{children && children()}
			</Modal>
		);
	}

	private onCancel = async () => {
		this.props.store.cancel();
	};

	private onClose = async () => {
		const type = EntityTypes[this.props.entityType];
		try {
			await this.props.store.store();
			const toastText = this.props.store.isNew ? getNewEntityText(type) : type.labelSingular;
			this.ctx.showToast("success", `${toastText} gespeichert`);
			this.props.navigate("/" + this.props.store.typeName + "/" + this.props.store!.id);
		} catch (error: any) {
			this.ctx.showAlert(
				"error",
				`Konnte ${type.labelSingular} nicht speichern: ` +
				(error.detail ? error.detail : error.title ? error.title : error)
			);
		}
	};

}

export default withRouter(ItemModal);
