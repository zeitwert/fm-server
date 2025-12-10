import { Button } from "@salesforce/design-system-react";
import React from "react";

interface ItemEditorButtonsProps {
	doEdit: boolean;
	allowStore: boolean;
	showEditButtons: boolean;
	onOpenEditor?: () => void;
	onCancelEditor: () => Promise<void>;
	onCloseEditor: () => Promise<void>;
	customButtons?: JSX.Element;
}

export class ItemEditorButtons extends React.Component<ItemEditorButtonsProps> {
	render() {
		const { showEditButtons, doEdit, allowStore, customButtons } = this.props;
		return (
			<>
				{
					showEditButtons &&
					<>
						{
							!doEdit &&
							<Button variant="icon" iconCategory="utility" iconName="edit" iconSize="medium" className="slds-m-top_x-small" onClick={this.props.onOpenEditor} />
						}
						{doEdit && <Button onClick={this.props.onCancelEditor}>Cancel</Button>}
						{
							doEdit && (
								<Button variant="brand" onClick={this.props.onCloseEditor} disabled={!allowStore}>
									Store
								</Button>
							)
						}
					</>
				}
				{customButtons}
			</>
		);
	}
}
