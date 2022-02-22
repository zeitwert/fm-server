import { EntityTypes } from "@comunas/ui-model";
import Icon from "@salesforce/design-system-react/components/icon";
import MediaObject from "@salesforce/design-system-react/components/media-object";
import Modal from "@salesforce/design-system-react/components/modal";
import { AppCtx } from "App";
import { action } from "mobx";
import { inject } from "mobx-react";
import React from "react";
import FormItemEditor, { BaseItemEditorProps } from "./FormItemEditor";
import { ItemEditorButtons } from "./ItemEditorButtons";

interface ItemModalProps extends BaseItemEditorProps {
	size?: "small" | "medium" | "large";
	children?: (form: JSX.Element | undefined) => JSX.Element;
}

@inject("appStore")
export default class ItemModal extends React.Component<ItemModalProps> {

	get ctx() {
		return this.props as any as AppCtx;
	}

	render() {
		const entityType = EntityTypes[this.props.entityType];
		const { iconCategory, iconName, labelSingular: itemName } = entityType;
		const { store, children } = this.props;
		const heading = (
			<MediaObject
				body={
					<>
						{store.isNew ? "Add new" : "Edit"} {itemName}
					</>
				}
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
				onCloseEditor={action(() => this.props.onClose(undefined!))}
			/>
		);
		return (
			<FormItemEditor {...Object.assign({}, this.props, { showEditButtons: true })}>
				{
					(editor) => {
						return (
							<Modal
								heading={heading}
								onRequestClose={this.props.onCancel}
								dismissOnClickOutside={false}
								footer={buttons}
								size={this.props.size || "small"}
								isOpen
							>
								{children && children(editor)}
								{!children && editor}
							</Modal>
						);
					}
				}
			</FormItemEditor>
		);
	}

}
