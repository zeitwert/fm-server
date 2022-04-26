import { Button, Combobox, Icon, Modal } from "@salesforce/design-system-react";
import { UserInfo } from "@zeitwert/ui-model";
import { AppCtx } from "App";
import { makeObservable, observable, toJS } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

interface ChangeOwnerModalProps {
	onSave: (user: UserInfo) => void;
	onCancel: () => void;
}

@inject("appStore", "logger", "session")
@observer
export default class ChangeOwnerModal extends React.Component<ChangeOwnerModalProps> {
	@observable users: UserInfo[] = [];
	@observable selectedUser?: UserInfo;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: ChangeOwnerModalProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		this.users = await this.ctx.appStore.getUsers();
	}

	render() {
		return (
			<Modal
				isOpen
				footer={[
					<Button key="cancel" label="Cancel" onClick={this.props.onCancel} />,
					<Button
						key="convert"
						label="Change"
						variant="brand"
						disabled={!this.selectedUser}
						onClick={() => this.props.onSave(toJS(this.users.find((u) => u.id === this.selectedUser!.id)!))}
					/>
				]}
				onRequestClose={this.props.onCancel}
				heading="Change Owner"
				size="small"
			>
				<section className="slds-p-around_large">
					<div className="slds-form-element slds-m-bottom_large">
						<Combobox
							labels={{
								label: "Owner",
								placeholderReadOnly: "Select Owner"
							}}
							options={this.users
								.filter((user) => this.ctx.session.sessionInfo?.user.id !== user.id)
								.map((user) => ({
									id: user.id,
									label: user.caption,
									icon: <Icon category="standard" name="user" size="small" />
								}))}
							events={{
								onSelect: (event: any, data: any) => (this.selectedUser = data.selection?.[0]),
								onRequestRemoveSelectedOption: (event: any, data: { selection?: any }) =>
									(this.selectedUser = undefined)
							}}
							value=""
							selection={this.selectedUser ? [this.selectedUser] : undefined}
							variant="inline-listbox"
							multiple={false}
							required
						/>
					</div>
					<div style={{ minHeight: "12rem" }} />
				</section>
			</Modal>
		);
	}
}
