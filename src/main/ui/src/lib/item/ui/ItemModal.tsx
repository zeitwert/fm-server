
import { Icon, MediaObject, Modal } from "@salesforce/design-system-react";
import { AggregateStore, EntityType, EntityTypes } from "@zeitwert/ui-model";
import { AppCtx } from "app/App";
import { action } from "mobx";
import { inject } from "mobx-react";
import React from "react";
import { ItemEditorButtons } from "./ItemEditorButtons";
import { getEditEntityText, getNewEntityText } from "./ItemUtils";

interface ItemModalProps {
	store: AggregateStore;
	entityType: EntityType;
	customButtons?: JSX.Element;
	size?: "small" | "medium" | "large";
	onOpen?: () => void;
	onCancel: () => Promise<void>;
	onClose: () => Promise<any>;
	children?: () => JSX.Element;
}

@inject("appStore")
export default class ItemModal extends React.Component<ItemModalProps> {

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
				onCancelEditor={action(() => this.props.onCancel())}
				onCloseEditor={action(() => this.props.onClose())}
			/>
		);
		return (
			<Modal
				heading={heading}
				onRequestClose={this.props.onCancel}
				dismissOnClickOutside={false}
				footer={buttons}
				size={this.props.size || "small"}
				isOpen
			>
				{children && children()}
			</Modal>
		);
	}

}
