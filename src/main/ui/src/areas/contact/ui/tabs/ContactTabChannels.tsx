
import { ContactStore } from "@zeitwert/ui-model";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";

interface ContactTabChannelsProps {
	store: ContactStore;
	displayMode: boolean;
}

@observer
export default class ContactTabChannels extends React.Component<ContactTabChannelsProps> {
	@observable isDisplayModeActive = false;

	constructor(props: ContactTabChannelsProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const contact = this.props.store.contact!;
		return (
			<div className="slds-m-around_medium">
				<div>Too bad {contact.caption}</div>
				{
					/*
				<FormWrapper
					formId="contact/editChannels"
					displayMode={this.props.displayMode ? "enabled" : "readonly"}
					onAfterChange={(path, value) => {
						this.onChange(path, value);
						return true;
					}}
					onSubformAdd={(path, value) => {
						if (path === "contact.interactionChannels") {
							contact.addAddress(
								Object.assign({}, value, {
									isMailAddress: false
								}),
								contact.addresses.length
							);
						}
					}}
					onSubformRemove={(path, value) => {
						if (path === "contact.interactionChannels") {
							contact.removeAddress(value.id);
						}
					}}
				/>
					*/
				}
			</div>
		);
	}

	// private onChange = async (path: string, value: any) => {
	// 	const { store } = this.props;
	// 	if (path && path.startsWith("contact.")) {
	// 		await store.item!.setField(path.substr("contact".length + 1), value);
	// 	}
	// };

}
