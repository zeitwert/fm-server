import { EntityType, LeadStoreModel, session } from "@zeitwert/ui-model";
import ItemsPage from "item/ui/ItemsPage";
import React from "react";
import { Route, Routes } from "react-router-dom";
import LeadPage from "./LeadPage";

export default class LeadArea extends React.Component {
	render() {
		session.setHelpContext(EntityType.LEAD);
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
							canCreate={session.isUser && !session.hasReadOnlyRole}
							createFormId="lead/editLead"
						/>
					}
				/>
				<Route path=":leadId" element={<LeadPage />} />
			</Routes>
		);
	}
}
