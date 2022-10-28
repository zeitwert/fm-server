
import { EntityType, session, User, UserStore, UserStoreModel } from "@zeitwert/ui-model";
import { withRouter } from "frame/app/withRouter";
import ItemsPage from "item/ui/ItemsPage";
import { inject, observer } from "mobx-react";
import React from "react";
import { Route, Routes } from "react-router-dom";
import UserCreationForm from "./forms/UserCreationForm";
import UserPage from "./UserPage";

const userStore = UserStoreModel.create({});

@inject("appStore", "session", "showAlert", "showToast")
@observer
class UserArea extends React.Component {

	render() {
		session.setHelpContext(EntityType.USER);
		return (
			<Routes>
				<Route
					path=""
					element={
						<ItemsPage
							entityType={EntityType.USER}
							store={userStore}
							listDatamart="oe.users"
							listTemplate="oe.users.all"
							actionButtons={this.getHeaderActions()}
							canCreate={session.isAdmin}
							createEditor={() => <UserCreationForm store={userStore} />}
							onAfterCreate={(store: UserStore) => { initUser(store.user!) }}
						/>
					}
				/>
				<Route path=":userId" element={<UserPage />} />
			</Routes>
		);
	}

	private getHeaderActions() {
		return [];
	}

}

export default withRouter(UserArea);

const initUser = (user: User) => {
	if (!session.isKernelTenant) {
		user.setField("tenant", session.sessionInfo?.tenant);
	}
	user.setField("role", "user");
}
