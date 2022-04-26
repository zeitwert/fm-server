
import { Icon, MediaObject } from "@salesforce/design-system-react";
import { Document } from "@zeitwert/ui-model";
import { DocumentUtils } from "dms/utils/DocumentUtils";
import { observer } from "mobx-react";
import React from "react";
import { Draggable } from "react-beautiful-dnd";

interface DocumentCatalogItemProps {
	document: Document;
	index: number;
	isSelected: boolean;
	setSelected: (document: Document) => void;
}

@observer
export class DocumentCatalogItem extends React.Component<DocumentCatalogItemProps> {
	render() {
		const { document, index, isSelected, setSelected } = this.props;
		const icon = DocumentUtils.fullIconName(document.contentTypeId);
		return (
			<Draggable draggableId={"TODO-" + document.id} index={index}>
				{({ innerRef, draggableProps, dragHandleProps }, snapshot) => {
					return (
						<tr
							ref={innerRef}
							{...draggableProps}
							{...dragHandleProps}
							//style={null/*MeetingUtils.draggingStyle(draggableProps.style, snapshot)*/}
							onClick={() => setSelected(document)}
							className={
								snapshot.isDragging
									? "fa-doc-catalog-dragging"
									: isSelected
										? "slds-card slds-card_boundary slds-is-selected"
										: "slds-card slds-card_boundary"
							}
						>
							<td>
								<MediaObject
									body={document.name}
									figure={
										<Icon
											category={icon.split(":")[0] as any}
											name={icon.split(":")[1]}
											size="small"
										/>
									}
									verticalCenter
								/>
							</td>
							<td>
								<div className="slds-truncate">{document.documentType?.name}</div>
							</td>
							{!snapshot.isDragging && (
								<td>
									<div className="slds-truncate">{document.description}</div>
								</td>
							)}
							<td>
								<div className="slds-truncate">{document.contentType?.name}</div>
							</td>
						</tr>
					);
				}}
			</Draggable>
		);
	}
}
