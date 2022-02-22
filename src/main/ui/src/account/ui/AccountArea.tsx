import { AccountStoreModel, EntityType } from "@comunas/ui-model";
import ItemsPage from "item/ui/ItemsPage";
import React from "react";
import { Route, Routes } from "react-router-dom";
import AccountPage from "./AccountPage";

export default class AccountArea extends React.Component {
	render() {
		return (
			<Routes>
				<Route
					path=""
					element={
						<ItemsPage
							entityType={EntityType.ACCOUNT}
							store={AccountStoreModel.create({})}
							listDatamart="account.accounts"
							listTemplate="account.accounts.all"
							canCreate
							createFormId="account/editAccount"
						/>
					}
				/>
				<Route path=":accountId" element={<AccountPage />} />
			</Routes>
		);
	}
}
