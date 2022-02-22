import { Document } from "@comunas/ui-model";
import { Col, Grid } from "@comunas/ui-slds/common/Grid";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";
import { Droppable } from "react-beautiful-dnd";
import { DocumentCatalogItem } from "./DocumentCatalogItem";

interface DocumentCatalogProps {
	documents: Document[];
}

@observer
export class DocumentCatalog extends React.Component<DocumentCatalogProps> {
	gridEl: any;
	@observable selectedDocument?: Document;

	constructor(props: DocumentCatalogProps) {
		super(props);
		makeObservable(this);
		this.gridEl = React.createRef();
	}

	render() {
		const { documents } = this.props;
		return (
			<Grid isVertical={false} ref={this.gridEl}>
				<Col>
					{!documents.length && <div className="slds-m-around_medium">No documents found.</div>}
					{documents.length > 0 && (
						<table
							className="slds-table slds-table_bordered slds-table_cell-buffer"
							style={{ maxWidth: "100%" }}
						>
							<thead>
								<tr className="slds-line-height_reset">
									<th scope="col">
										<div className="slds-truncate">Name</div>
									</th>
									<th scope="col">
										<div className="slds-truncate">Type</div>
									</th>
									<th scope="col">
										<div className="slds-truncate">Description</div>
									</th>
									<th scope="col">
										<div className="slds-truncate">Content</div>
									</th>
								</tr>
							</thead>
							<Droppable
								droppableId={"TODO-TODO-TODO"} // TODO
								isDropDisabled
								getContainerForClone={() => this.gridEl.current}
							>
								{(provided) => (
									<tbody ref={provided.innerRef} {...provided.droppableProps}>
										{documents.map((document, i) => (
											<DocumentCatalogItem
												document={document}
												key={document.id}
												index={i}
												setSelected={this.setSelectedDocument}
												isSelected={
													!!this.selectedDocument && document.id === this.selectedDocument.id
												}
											/>
										))}
										{provided.placeholder}
									</tbody>
								)}
							</Droppable>
						</table>
					)}
				</Col>
			</Grid>
		);
	}

	private setSelectedDocument = async (document: Document) => {
		this.selectedDocument = document;
	};
}
