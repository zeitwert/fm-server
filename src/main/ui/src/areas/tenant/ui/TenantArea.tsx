
import { EntityType, session, Tenant, TenantStore, TenantStoreModel } from "@zeitwert/ui-model";
import { withRouter } from "app/frame/withRouter";
import ItemsPage from "lib/item/ui/ItemsPage";
import { observer } from "mobx-react";
import React from "react";
import { Route, Routes } from "react-router-dom";
import TenantCreationForm from "./TenantCreationForm";
import TenantPage from "./TenantPage";

const tenantStore = TenantStoreModel.create({});

@observer
class TenantArea extends React.Component {

	componentDidMount(): void {
		session.setHelpContext(EntityType.TENANT);
	}

	render() {
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
							createEditor={() => <TenantCreationForm tenant={tenantStore.tenant!} />}
							onAfterCreate={(store: TenantStore) => { initTenant(store.tenant!) }}
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
