
import { Icon, MediaObject, Modal } from "@salesforce/design-system-react";
import { EntityType, EntityTypes } from "@zeitwert/ui-model";
import React from "react";

interface PartModalProps {
	entityType: EntityType;
	isNew: boolean;
	onCancel: () => Promise<void>;
	onClose: () => Promise<any>;
	size?: "small" | "medium" | "large";
	children?: JSX.Element;
}

export default class PartModal extends React.Component<PartModalProps> {

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
