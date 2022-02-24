import Icon from "@salesforce/design-system-react/components/icon";
import MediaObject from "@salesforce/design-system-react/components/media-object";
import Modal from "@salesforce/design-system-react/components/modal";
import { EntityType, EntityTypes } from "@zeitwert/ui-model";
import { AppCtx } from "App";
import { inject } from "mobx-react";
import React from "react";

interface PartModalProps {
	entityType: EntityType;
	isNew: boolean;
	onCancel: () => Promise<void>;
	onClose: () => Promise<any>;
	size?: "small" | "medium" | "large";
	children?: JSX.Element;
}

@inject("appStore")
export default class PartModal extends React.Component<PartModalProps> {

	get ctx() {
		return this.props as any as AppCtx;
	}

	render() {
		const entityType = EntityTypes[this.props.entityType];
		const { iconCategory, iconName, labelSingular: itemName } = entityType;
		const { isNew, children } = this.props;
		const heading = (
			<MediaObject
				body={
					<>
						{isNew ? "Add new" : "Edit"} {itemName}
					</>
				}
				figure={<Icon category={iconCategory as any} name={iconName as any} size="small" />}
				verticalCenter
			/>
		);
		return (
			<Modal
				heading={heading}
				onRequestClose={this.props.onCancel}
				dismissOnClickOutside={false}
				size={this.props.size || "small"}
				isOpen
			>
				<div>
					{children}
				</div>
			</Modal>
		);
	}

}
