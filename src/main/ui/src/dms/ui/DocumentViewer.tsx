
import Button from "@salesforce/design-system-react/components/button";
import Modal from "@salesforce/design-system-react/components/modal";
import { Config, decodeHtml, Document } from "@zeitwert/ui-model";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";
import PDFViewer from "./pdf/PDFViewer";

const API_BASE_URL = Config.getApiUrl("##", "##").replace("/##/##", "");

enum DocumentState {
	Initial = 1,
	Loading,
	Ready
}

interface DocumentViewerProps {
	className?: any;
	document: Document;
	doEdit: boolean;
	doModal?: boolean | false;
	onCancel?: () => void;
	onClose?: () => void;
	onPage?(page: number): void; // questionnaire: slide to page
	page?: number; // outside page control
	onDocumentComplete?(pageCount: number): void; // outside page control
	onItemClick?(page: number): void; // outside page control
}

@observer
export class DocumentViewer extends React.Component<DocumentViewerProps> {
	@observable private documentState: DocumentState = DocumentState.Initial;
	PDFViewer: PDFViewer | null = null;
	el: any;

	constructor(props: DocumentViewerProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		this.loadDocument();
	}

	async componentDidUpdate(prevProps: Readonly<DocumentViewerProps>) {
		if (prevProps.document?.id !== this.props.document?.id) {
			this.loadDocument();
		}
	}

	render() {
		const { className, document, doModal, onCancel } = this.props;
		if (!document) {
			return <></>;
		}
		if (doModal) {
			return (
				<Modal
					heading={document.completeName}
					isOpen
					dismissOnClickOutside={false}
					onRequestClose={onCancel}
					footer={[
						<Button key="cancel" label="Cancel" onClick={onCancel} />,
						<Button key="save" label="Save" variant="brand" onClick={this.closeDocument} />
					]}
					size="large"
					contentClassName="slds-p-around_medium fa-height-90-100"
				>
					{this.getViewer()}
				</Modal>
			);
		}
		// TODO: lightning not imported in plugin, so can't use classNames.
		const classes = (className ? className : "") + " slds-is-relative fa-height-100";
		return (
			<div className={classes} ref={(el) => (this.el = el)}>
				{this.getViewer()}
			</div>
		);
	}

	private loadDocument = async () => {
		const document = this.props.document;
		if (!document || this.documentState === DocumentState.Ready) {
			return;
		}
		if (document.isDocument) {
			this.documentState = DocumentState.Loading;
		}
	};

	private getViewer = () => {
		const document = this.props.document;
		if (this.documentState === DocumentState.Initial) {
			return <>Loading...</>;
		}
		if (document.isPdf) {
			return (
				<div className="slds-scrollable_x">
					<PDFViewer
						file={document.isUrl ? document?.url || "" : API_BASE_URL + document.content?.downloadUrl}
						onLoad={() => (this.documentState = DocumentState.Ready)}
						ref={(component) => (this.PDFViewer = component)}
						onDocumentComplete={this.props.onDocumentComplete}
						onItemClick={(page) => this.props.onItemClick?.(page)}
						page={this.props.page}
					/>
				</div>
			);
		} else if (document.isDocument && document.content) {
			<div className="fa-full-dims" style={{ minHeight: 800 }}>
				<iframe
					title="AgendaItem"
					src={API_BASE_URL + document.content.downloadUrl || ""}
					className="fa-full-dims fa-border-0"
					style={{ minHeight: 800 }}
				/>
			</div>;
		} else if (document.isUrl) {
			return (
				<div className="slds-scrollable_x">
					<iframe
						className="fa-border-0 fa-full-width slds-scrollable_y"
						title="Document"
						src={decodeHtml(document?.url || "")}
						style={{ height: this.el?.offsetHeight }}
					/>
				</div>
			);
		}
		return <div className="slds-m-around_medium">Unable to load document.</div>;
	};

	private closeDocument = () => {
		this.props.onClose && this.props.onClose();
	};
}
