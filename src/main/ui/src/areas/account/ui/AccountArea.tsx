import { Account, AccountStore, AccountStoreModel, EntityType, session } from "@zeitwert/ui-model";
import ItemsPage from "lib/item/ui/ItemsPage";
import React from "react";
import { Route, Routes } from "react-router-dom";
import AccountCreationForm from "./AccountCreationForm";
import AccountPage from "./AccountPage";

const accountStore = AccountStoreModel.create({});

export default class AccountArea extends React.Component {

	componentDidMount(): void {
		session.setHelpContext(EntityType.ACCOUNT);
	}

	render() {
		return (
			<Routes>
				<Route
					path=""
					element={
						<ItemsPage
							entityType={EntityType.ACCOUNT}
							store={accountStore}
							listDatamart="account.accounts"
							listTemplate="account.accounts.all"
							actionButtons={this.getHeaderActions()}
							canCreate={session.isAdmin && (session.isKernelTenant || session.isAdvisorTenant)}
							createEditor={() => <AccountCreationForm store={accountStore} />}
							onAfterCreate={(store: AccountStore) => { initAccount(store.account!) }}
						/>
					}
				/>
				<Route path=":accountId" element={<AccountPage />} />
			</Routes>
		);
	}

	private getHeaderActions() {
		return [];
	}

}

const initAccount = (account: Account) => {
	if (!session.isKernelTenant) {
		account.setField("tenant", session.sessionInfo?.tenant);
	}
}
