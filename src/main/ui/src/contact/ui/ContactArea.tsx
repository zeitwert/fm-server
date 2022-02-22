import { ContactStoreModel, EntityType } from "@comunas/ui-model";
import ItemsPage from "item/ui/ItemsPage";
import React from "react";
import { Route, Routes } from "react-router-dom";
import ContactPage from "./ContactPage";

export default class ContactArea extends React.Component {
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
							canCreate
							createFormId="contact/editContact"
						/>
					}
				/>
				<Route path=":contactId" element={<ContactPage />} />
			</Routes>
		);
	}
}
