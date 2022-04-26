import { Combobox, Icon } from "@salesforce/design-system-react";
import { Contact } from "@zeitwert/ui-model";
import { observer } from "mobx-react";
import React from "react";

type GenericContact = Contact;

interface ContactComboboxProps {
	contact?: GenericContact;
	contacts?: GenericContact[];
	onSelectContact: (contact?: Contact) => void;
}

@observer
export default class ContactCombobox extends React.Component<ContactComboboxProps> {
	options: any[] = [];

	constructor(props: ContactComboboxProps) {
		super(props);
		this.options = this.props.contacts?.map((c) => this.convertContact(c)) || [];
	}

	render() {
		const { contact, contacts, onSelectContact } = this.props;
		return (
			<Combobox
				labels={{
					label: "Contact"
				}}
				events={{
					onSelect: (event: any, data: any) =>
						onSelectContact(contacts?.find((p) => p.id === data.selection[0].id) as Contact),
					onRequestRemoveSelectedOption: () => onSelectContact(undefined)
				}}
				value=""
				selection={contact ? [this.convertContact(contact)] : undefined}
				options={this.options}
				variant="inline-listbox"
			/>
		);
	}

	private convertContact(contact: GenericContact) {
		return {
			id: contact.id,
			label: contact.caption,
			subTitle: contact.owner.caption,
			icon: <Icon category="standard" name="contact" size="small" />
		};
	}
}
