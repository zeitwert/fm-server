import { AggregateStore, EntityType, ItemPartStore } from "@zeitwert/ui-model";
import { AppCtx } from "app/App";
import { action, computed, makeObservable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import { ItemEditorButtons } from "./ItemEditorButtons";

export interface BaseItemEditorProps {
	store: AggregateStore | ItemPartStore;
	entityType: EntityType;
	onCancel: () => Promise<void>;
	onClose: () => Promise<any>;
}

interface FormItemEditorProps extends BaseItemEditorProps {
	showEditButtons?: boolean;
	customButtons?: JSX.Element;
	children: () => JSX.Element;
	onOpen?: () => void;
}

@inject("appStore", "session")
@observer
export default class FormItemEditor extends React.Component<FormItemEditorProps> {

	@computed get isFormDisabled() {
		return !this.props.store?.item?.allowStore;
	}

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: FormItemEditorProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const { store } = this.props;
		const { showEditButtons, customButtons, children, } = this.props;
		const buttons = (
			<ItemEditorButtons
				showEditButtons={showEditButtons || false}
				doEdit={store.isInTrx}
				allowStore={!this.isFormDisabled}
				onOpenEditor={action(() => this.onOpen())}
				onCancelEditor={action(() => this.onCancel())}
				onCloseEditor={action(() => this.onClose())}
				customButtons={customButtons}
			/>
		);
		return (
			<>
				<div className="slds-m-horizontal_medium slds-text-align_right" style={{ position: "absolute", right: "0", top: "3px" }}>
					{buttons}
				</div>
				{children()}
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
		const item = await this.props.onClose();
		if (item) {
			this.ctx.appStore.setItem(item);
		}
	};

}
