
import { Combobox, Icon } from "@salesforce/design-system-react";
import { Account, GenericUser, GenericUserType, UserInfo } from "@zeitwert/ui-model";
import { AppCtx } from "app/App";
import { observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

export interface ComboboxItem {
	id: string;
	label: string;
	subTitle?: string;
	icon: React.ReactElement;
	type?: any;
}

interface GenericUserComboboxProps {
	labels?: any;
	account?: Account;
	items: ComboboxItem[];
	isMultiple?: boolean;
	isRequired?: boolean;
	isDisabled?: boolean;
	onChange: (genericUsers: ComboboxItem[]) => void;
}

@inject("appStore", "logger")
@observer
export default class GenericUserCombobox extends React.Component<GenericUserComboboxProps> {

	@observable users: UserInfo[] = [];

	get ctx() {
		return this.props as any as AppCtx;
	}

	async componentDidMount() {
		this.users = await this.ctx.appStore.getUsers();
	}

	render() {
		const { labels, account, items, isMultiple, isRequired, isDisabled, onChange } = this.props;
		const options = account?.contacts
			.map((c) => this.convertItem(c, GenericUserType.Contact))
			.concat(this.users.map((u: UserInfo) => this.convertItem(u, GenericUserType.User)))
			.filter((r) => items.findIndex((it) => it.id === r.id) === -1);
		return (
			<Combobox
				labels={labels}
				events={{
					onSelect: (event: any, data: { selection?: ComboboxItem[] }) => onChange(data.selection || []),
					onRequestRemoveSelectedOption: (event: any, data: { selection?: ComboboxItem[] }) =>
						onChange(data.selection || [])
				}}
				value=""
				selection={items}
				options={options}
				variant="inline-listbox"
				singleInputDisabled={isDisabled}
				required={isRequired}
				multiple={isMultiple}
			/>
		);
	}

	private convertItem(genericUser: GenericUser, type: GenericUserType) {
		return {
			id: genericUser.id,
			label: genericUser.name,
			icon: <Icon category="standard" name={type} size="small" />,
			type: type
		} as ComboboxItem;
	}

}
