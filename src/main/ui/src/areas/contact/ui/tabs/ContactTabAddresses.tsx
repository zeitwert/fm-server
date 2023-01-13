import { FormWrapper } from "@zeitwert/ui-forms";
import { ContactStore } from "@zeitwert/ui-model";
import { observer } from "mobx-react";
import React from "react";

interface ContactTabAddressesProps {
	store: ContactStore;
	displayMode: boolean;
}

@observer
export default class ContactTabAddresses extends React.Component<ContactTabAddressesProps> {
	render() {
		const contact = this.props.store.contact!;
		return (
			<div className="slds-m-around_medium">
				<FormWrapper
					formId="contact/editAddresses"
					displayMode={this.props.displayMode ? "enabled" : "readonly"}
					onAfterChange={(path, value) => {
						this.onChange(path, value);
						return true;
					}}
					onSubformAdd={(path, value) => {
						if (path === "contact.postalAddresses") {
							contact.addAddress(
								Object.assign({}, value, {
									isMailAddress: true
								}),
								contact.addresses.length
							);
						}
					}}
					onSubformRemove={(path, value) => {
						if (path === "contact.postalAddresses") {
							contact.removeAddress(value.id);
						}
					}}
				/>
			</div>
		);
	}

	private onChange = async (path: string, value: any) => {
		const { store } = this.props;
		if (path && path.startsWith("contact.")) {
			await store.item!.setField(path.substr("contact".length + 1), value);
		}
	};
}
