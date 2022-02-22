import { AccountStore, UserInfo } from "@comunas/ui-model";
import Button from "@salesforce/design-system-react/components/button";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import { AppCtx } from "../../App";
import ChangeOwnerModal from "./ChangeOwnerModal";

interface ChangeOwnerButtonProps {
	accountStore: AccountStore;
}

@inject("showAlert", "showToast")
@observer
export default class ChangeOwnerButton extends React.Component<ChangeOwnerButtonProps> {
	@observable doChangeOwner = false;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: ChangeOwnerButtonProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		return (
			<>
				<Button onClick={this.openChangeOwnerModal}>Change Owner</Button>
				{this.doChangeOwner && (
					<ChangeOwnerModal onSave={this.closeChangeOwnerModal} onCancel={this.cancelChangeOwnerModal} />
				)}
			</>
		);
	}

	private openChangeOwnerModal = async () => {
		this.doChangeOwner = true;
	};

	private cancelChangeOwnerModal = () => {
		this.doChangeOwner = false;
	};

	private closeChangeOwnerModal = async (user: UserInfo) => {
		try {
			//await this.props.accountStore.changeOwner(user);
			this.ctx.showToast("success", `Owner changed`);
			this.doChangeOwner = false;
		} catch (error: any) {
			this.ctx.showAlert(
				"error",
				"Could not change Owner: " + (error.detail ? error.detail : error.title ? error.title : error)
			);
		}
	};
}
