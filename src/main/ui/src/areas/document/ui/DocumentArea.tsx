import { DocumentStoreModel, EntityType, session } from "@zeitwert/ui-model";
import ItemsPage from "lib/item/ui/ItemsPage";
import React from "react";
import { Route, Routes } from "react-router-dom";
import DocumentPage from "./DocumentPage";

const documentStore = DocumentStoreModel.create({});

export default class DocumentArea extends React.Component {

	componentDidMount(): void {
		session.setHelpContext(EntityType.DOCUMENT);
	}

	render() {
		return (
			<Routes>
				<Route
					path=""
					element={
						<ItemsPage
							entityType={EntityType.DOCUMENT}
							store={documentStore}
							listDatamart="dms.documents"
							listTemplate="dms.documents.all"
							canCreate={false && !session.hasReadOnlyRole}
						/>
					}
				/>
				<Route path=":documentId" element={<DocumentPage />} />
			</Routes>
		);
	}

}
