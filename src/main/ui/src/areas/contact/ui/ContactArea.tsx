import { ContactStoreModel, EntityType, session } from "@zeitwert/ui-model";
import ItemsPage from "lib/item/ui/ItemsPage";
import React from "react";
import { Route, Routes } from "react-router-dom";
import ContactPage from "./ContactPage";

export default class ContactArea extends React.Component {

	componentDidMount(): void {
		session.setHelpContext(EntityType.CONTACT);
	}

	render() {
		return (
			<Routes>
				<Route
					path=""
					element={
						<ItemsPage
							entityType={EntityType.CONTACT}
							store={ContactStoreModel.create({})}
							listDatamart="contact.contacts"
							listTemplate="contact.contacts.all"
							canCreate={session.isUser && !session.hasReadOnlyRole}
							createFormId="contact/editContact"
						/>
					}
				/>
				<Route path=":contactId" element={<ContactPage />} />
			</Routes>
		);
	}

}
