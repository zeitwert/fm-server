
import { EntityType, session, Tenant, TenantStore, TenantStoreModel } from "@zeitwert/ui-model";
import { withRouter } from "app/frame/withRouter";
import ItemsPage from "lib/item/ui/ItemsPage";
import { inject, observer } from "mobx-react";
import React from "react";
import { Route, Routes } from "react-router-dom";
import TenantCreationForm from "./TenantCreationForm";
import TenantPage from "./TenantPage";

const tenantStore = TenantStoreModel.create({});

@inject("appStore", "session", "showAlert", "showToast")
@observer
class TenantArea extends React.Component {

	render() {
		session.setHelpContext(EntityType.TENANT);
		return (
			<Routes>
				<Route
					path=""
					element={
						<ItemsPage
							entityType={EntityType.TENANT}
							store={tenantStore}
							listDatamart="oe.tenants"
							listTemplate="oe.tenants.all"
							actionButtons={this.getHeaderActions()}
							canCreate={session.isAdmin && session.isKernelTenant}
							createEditor={() => <TenantCreationForm store={tenantStore} />}
							onAfterCreate={(store: TenantStore) => { initTenant(store.item!) }}
						/>
					}
				/>
				<Route path=":tenantId" element={<TenantPage />} />
			</Routes>
		);
	}

	private getHeaderActions() {
		return [];
	}

}

export default withRouter(TenantArea);

const initTenant = (tenant: Tenant) => {
}
