import { AggregateStore, EntityType } from "@comunas/ui-model";
import { AppCtx } from "App";
import { action } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import { ItemEditorButtons } from "./ItemEditorButtons";

interface ItemEditorProps {
	store: AggregateStore;
	entityType: EntityType;
	showEditButtons?: boolean;
	customButtons?: JSX.Element;
	onOpen?: () => void;
	onCancel: () => Promise<void>;
	onClose: () => Promise<any>;
}

@inject("appStore", "session")
@observer
export default class ItemEditor extends React.Component<ItemEditorProps> {

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
				showEditButtons={showEditButtons || false}
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
		this.props.onOpen && this.props.onOpen();
	};

	private onCancel = async () => {
		await this.props.onCancel();
	};

	private onClose = async () => {
		await this.props.onClose();
	};

}
