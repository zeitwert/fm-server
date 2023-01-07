import { computed, makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";
import { Page } from "react-pdf";
import { Document } from "react-pdf/dist/esm/entry.webpack";

interface PDFViewerProps {
	file: string;
	page?: number;
	onDocumentComplete?(pages: number): void;
	onLoad?(): void;
	onItemClick?(page: number): void;
}

@observer
export default class PDFViewer extends React.Component<PDFViewerProps> {
	@observable pageCount = 1;
	@observable page = 1;

	constructor(props: PDFViewerProps) {
		super(props);
		makeObservable(this);
	}

	componentDidMount() {
		this.page = this.props.page ? this.props.page : 1;
	}

	async componentDidUpdate() {
		this.page = this.props.page ? this.props.page : this.page;
	}

	@computed get isFirstPage() {
		return this.page === 1;
	}

	@computed get isLastPage() {
		return this.page === this.pageCount;
	}

	public prevPage() {
		if (this.page > 1) {
			this.page = this.page - 1;
		}
	}

	public nextPage() {
		if (this.page < this.pageCount) {
			this.page = this.page + 1;
		}
	}

	private onDocumentComplete = (pdf: any) => {
		this.pageCount = pdf.numPages;
		this.props.onDocumentComplete && this.props.onDocumentComplete(pdf.numPages);
		this.props.onLoad && this.props.onLoad();
	};

	private onItemClick = (pdf: any) => {
		this.props.onItemClick && this.props.onItemClick(pdf.pageNumber);
	};

	render() {
		const { file, page, onLoad } = this.props;
		return (
			<Document
				className="slds-m-around_medium"
				file={file}
				onLoadSuccess={this.onDocumentComplete}
				onLoadError={() => onLoad && onLoad()}
				onSourceSuccess={() => onLoad && onLoad()}
				onSourceError={() => onLoad && onLoad()}
				onItemClick={this.onItemClick}
			>
				{!page &&
					Array.from(new Array(this.pageCount), (el, index) => (
						<Page key={`page_${index}`} pageIndex={index} width={1000} />
					))}
				{page && <Page className="fa-pdf-page" pageIndex={this.page - 1} width={1000} />}
			</Document>
		);
	}
}
