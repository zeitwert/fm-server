import { DocumentStoreModel, EntityType, session } from "@zeitwert/ui-model";
import ItemsPage from "item/ui/ItemsPage";
import React from "react";
import { Route, Routes } from "react-router-dom";
import DocumentPage from "./DocumentPage";

export default class DocumentArea extends React.Component {
	render() {
		session.setHelpContext(EntityType.DOCUMENT);
		return (
			<Routes>
				<Route
					path=""
					element={
						<ItemsPage
							entityType={EntityType.DOCUMENT}
							store={DocumentStoreModel.create({})}
							listDatamart="dms.documents"
							listTemplate="dms.documents.all"
							canCreate={!session.hasReadOnlyRole}
							createFormId="dms/editDocument"
						/>
					}
				/>
				<Route path=":documentId" element={<DocumentPage />} />
			</Routes>
		);
	}
}
