import { Card } from "@salesforce/design-system-react";
import { FormWrapper } from "@zeitwert/ui-forms";
import { AggregateStore, EntityType, ItemPartStore } from "@zeitwert/ui-model";
import { AppCtx } from "frame/App";
import { action, computed, makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import { ItemEditorButtons } from "./ItemEditorButtons";

export interface BaseItemEditorProps {
	store: AggregateStore | ItemPartStore;
	entityType: EntityType;
	formId?: string;
	formDefinition?: any;
	itemAlias?: string;
	control?: any;
	onCancel: () => Promise<void>;
	onClose: (api: FormApi) => Promise<any>;
	onChange?: (path: string, value: any) => void; // Exposed form method.
	onButtonClick?: (path: string, script: string) => boolean | void; // Exposed form method.
}

interface FormItemEditorProps extends BaseItemEditorProps {
	showEditButtons?: boolean;
	customButtons?: JSX.Element;
	children: (editor: JSX.Element | undefined) => JSX.Element;
	onOpen?: () => void;
}

type FormApi = any;

@inject("appStore", "session")
@observer
export default class FormItemEditor extends React.Component<FormItemEditorProps> {

	@observable.shallow control?: any;
	@observable formApi?: FormApi;
	@observable isFormValid = true;
	@observable isFormProcessing = false;

	@computed get isFormDisabled() {
		return !this.isFormValid || this.isFormProcessing || !this.props.store?.item?.allowStore;
	}

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: FormItemEditorProps) {
		super(props);
		makeObservable(this);
		this.control = this.props.control || {};
		// document.addEventListener("keydown", (e) => {
		// 	if (e.key === "s" && e.ctrlKey) {
		// 		e.preventDefault();
		// 	}
		// })
	}

	render() {
		const {
			formId,
			formDefinition,
			itemAlias,
			store,
			onButtonClick
		} = this.props;
		let editor: JSX.Element | undefined;
		if (!!formId || !!formDefinition) {
			editor = (
				<Card heading="" hasNoHeader bodyClassName="slds-m-around_medium">
					<FormWrapper
						formId={formId}
						formDefinition={formDefinition}
						payload={{
							[itemAlias!]: store.item?.formSnapshot,
							control: Object.assign({}, this.control, {
								enabled: store.isInTrx
							})
						}}
						displayMode={store.isInTrx ? undefined : "readonly"}
						onReady={(api: FormApi) => (this.formApi = api)}
						onAfterChange={(path: any, value: any) => {
							this.control = Object.assign({}, this.formApi!.payload.get("control"));
							this.onChange(path, value);
							return true;
						}}
						onButtonClick={onButtonClick}
						onValidChange={(isValid: boolean) => (this.isFormValid = isValid)}
					/>
				</Card>
			);
		}
		const {
			showEditButtons,
			customButtons,
			children,
		} = this.props;
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
				{children(editor)}
			</>
		);
	}

	private onChange = async (path: string, value: any) => {
		const { itemAlias, store, onChange } = this.props;
		if (path && path.startsWith(itemAlias + ".")) {
			await store.item!.setField(path.substr(itemAlias!.length + 1), value);
		}
		await (onChange && onChange(path, value));
	};

	private onOpen = () => {
		this.props.onOpen && this.props.onOpen();
	};

	private onCancel = async () => {
		this.isFormProcessing = true;
		await this.props.onCancel();
		this.isFormProcessing = false;
	};

	private onClose = async () => {
		this.isFormProcessing = true;
		const item = await this.props.onClose(this.formApi!);
		if (item) {
			this.ctx.appStore.setItem(item);
		}
		this.control = this.props.control || {};
		this.isFormProcessing = false;
	};

}
