import { Account, AccountStore, AccountStoreModel, EntityType, session } from "@zeitwert/ui-model";
import { withRouter } from "app/frame/withRouter";
import ItemsPage from "lib/item/ui/ItemsPage";
import { inject, observer } from "mobx-react";
import React from "react";
import { Route, Routes } from "react-router-dom";
import AccountCreationForm from "./AccountCreationForm";
import AccountPage from "./AccountPage";

const accountStore = AccountStoreModel.create({});

@inject("appStore", "session", "showAlert", "showToast")
@observer
class AccountArea extends React.Component {

	render() {
		session.setHelpContext(EntityType.ACCOUNT);
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

export default withRouter(AccountArea);

const initAccount = (user: Account) => {
	if (!session.isKernelTenant) {
		user.setField("tenant", session.sessionInfo?.tenant);
	}
}
