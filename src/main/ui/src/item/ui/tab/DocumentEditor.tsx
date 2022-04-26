
import { Icon, SplitView, SplitViewHeader } from "@salesforce/design-system-react";
import {
	AggregateStore, API,
	Config,
	Document,
	DocumentContentSnapshot,
	DocumentModel,
	DocumentStoreModel,
	DOCUMENT_API, Enumerated
} from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds/common/Grid";
import { AppCtx } from "App";
import { DocumentCatalog } from "dms/ui/DocumentCatalog";
import DocumentsWidget from "dms/ui/DocumentsWidget";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import { DragDropContext, Droppable, DropResult } from "react-beautiful-dnd";
import { ItemFilter } from "../ItemFilter";

interface DocumentEditorProps {
	store: AggregateStore;
	documents: Document[];
	areas?: Enumerated[];
	onAdd: (document: Document) => void;
	onSet: (documents: Document[]) => void;
}

@inject("logger")
@observer
export default class DocumentEditor extends React.Component<DocumentEditorProps> {
	@observable availableDocuments: Document[] = [];
	@observable selectedDocument?: Document;
	@observable areasOptions: any[] = [];

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: DocumentEditorProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		await this.loadAreas();
		this.availableDocuments = await DocumentStoreModel.create({}).getAvailableDocuments();
	}

	render() {
		const { documents, areas } = this.props;
		return (
			<DragDropContext onDragEnd={(result) => this.onDrop(result)}>
				<SplitView
					masterWidth="30rem"
					master={
						<>
							<SplitViewHeader
								// @ts-ignore
								className="fa-border-0"
								icon={<Icon category="standard" name="document" />}
								title="Documents"
								info={documents.length + " items"}
								truncate
							/>
							<Grid className="slds-scrollable_y slds-p-around_medium fa-height-100">
								<Col>
									<Droppable droppableId={"TODO-TODO-TODO"}>
										{(provided, snapshot) => (
											<div
												ref={provided.innerRef}
												{...provided.droppableProps}
												className={snapshot.isDraggingOver ? "fa-document-dragging" : ""}
											>
												<DocumentsWidget
													documents={documents}
													onChange={(value) => this.setDocuments(value)}
												/>
											</div>
										)}
									</Droppable>
								</Col>
							</Grid>
						</>
					}
					detail={
						<ItemFilter
							availableItems={this.availableDocuments}
							selectedAreas={areas}
							areasOptions={this.areasOptions}
						>
							{(filteredItems: Document[]) => <DocumentCatalog documents={filteredItems} />}
						</ItemFilter>
					}
					isOpen
				/>
			</DragDropContext>
		);
	}

	private async setDocuments(content: DocumentContentSnapshot[]) {
		const { documents, onSet } = this.props;
		const docs = [];
		for (const c of content) {
			if (c.objId) {
				const documentId = c.objId.toString();
				let doc = documents.find((doc) => doc.id === documentId);
				if (!doc) {
					const repository = await DOCUMENT_API.loadAggregate(documentId);
					doc = DocumentModel.create(repository.document[documentId]);
				}
				doc.syncMetadata(c);
				docs.push(doc);
			}
		}
		onSet(docs);
	}

	private onDrop = async (result: DropResult) => {
		const { destination, draggableId, source } = result;
		const { documents, onAdd } = this.props;

		if (!destination) {
			return;
		}

		if (source.droppableId.substr(0, "TODO-TODO-TODO".length) === "TODO-TODO-TODO") {
			// Adding doc item catalog.
			const documentId = draggableId.substr("TODO-".length, draggableId.length);
			const template = this.availableDocuments.find((doc) => doc.id === documentId);
			if (template && documents.findIndex((doc) => doc.id === documentId) === -1) {
				onAdd(DocumentModel.create(template.formSnapshot) as Document);
			}
		}
	};

	private async loadAreas() {
		try {
			const response = await API.get(Config.getEnumUrl("base", "codeArea"));
			this.areasOptions = response.data.map((item: any) => ({
				value: item.id,
				label: item.name
			}));
		} catch (error: any) {
			this.ctx.logger.error("Couldn't load areas", error);
		}
	}
}
