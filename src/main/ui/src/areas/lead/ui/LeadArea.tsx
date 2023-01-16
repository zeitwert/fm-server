import { EntityType, LeadStoreModel, session } from "@zeitwert/ui-model";
import ItemsPage from "lib/item/ui/ItemsPage";
import React from "react";
import { Route, Routes } from "react-router-dom";
import LeadPage from "./LeadPage";

const leadStore = LeadStoreModel.create({});

export default class LeadArea extends React.Component {

	componentDidMount(): void {
		session.setHelpContext(EntityType.LEAD);
	}

	render() {
		return (
			<Routes>
				<Route
					path=""
					element={
						<ItemsPage
							entityType={EntityType.LEAD}
							store={leadStore}
							listDatamart="lead.leads"
							listTemplate="lead.leads.my-open"
							canCreate={false && session.isUser && !session.hasReadOnlyRole}
						/>
					}
				/>
				<Route path=":leadId" element={<LeadPage />} />
			</Routes>
		);
	}

}
