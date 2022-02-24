import { EntityType, LeadStoreModel } from "@zeitwert/ui-model";
import ItemsPage from "item/ui/ItemsPage";
import React from "react";
import { Route, Routes } from "react-router-dom";
import LeadPage from "./LeadPage";

export default class LeadArea extends React.Component {
	render() {
		return (
			<Routes>
				<Route
					path=""
					element={
						<ItemsPage
							entityType={EntityType.LEAD}
							store={LeadStoreModel.create({})}
							listDatamart="lead.leads"
							listTemplate="lead.leads.my-open"
							canCreate
							createFormId="lead/editLead"
						/>
					}
				/>
				<Route path=":leadId" element={<LeadPage />} />
			</Routes>
		);
	}
}
