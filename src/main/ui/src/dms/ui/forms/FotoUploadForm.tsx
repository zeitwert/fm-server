
import { Button, Spinner } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow } from "@zeitwert/ui-forms";
import { API, Config } from "@zeitwert/ui-model";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";

export interface FotoUploadFormProps {
	documentId: string | undefined;
	documentContentUrl: string | undefined; // GET | POST
	supportedContentTypes: string | undefined;
	afterUpload: () => void;
	title: string;
	minHeight?: string;
}

@observer
export default class FotoUploadForm extends React.Component<FotoUploadFormProps> {

	imageFile: File | undefined = new File([], "empty");
	@observable imageFileSeqNr: number = 1;
	@observable isUploading: boolean = false;
	@observable hasUploadError: boolean = false;

	constructor(props: FotoUploadFormProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		console.log("render", this.props.title, this.props.documentId, this.imageFile);
		const minHeight = this.props.minHeight || "200px";
		if (this.isUploading) {
			return <Spinner variant="brand" size="large" />;
		} else if (this.hasUploadError) {
			return <UploadAlert clearError={() => this.hasUploadError = false} />;
		} else if (!this.props.documentId) {
			return (
				<>
					{
						this.props.title &&
						<div className="slds-p-bottom_small" style={{ fontWeight: 700 }}>{this.props.title}</div>
					}
					<div style={{ width: "100%", minHeight: minHeight, backgroundColor: "#dddddd", padding: "10px" }}>
						<p>Das Foto-Dokument wurde noch nicht angelegt.</p>
						<p>&nbsp;</p>
						<p><b>Bitte Objekt bearbeiten und speichern.</b></p>
					</div>
				</>
			);
		}
		const imageUrl = this.imageFileSeqNr && this.imageFile?.size ? URL.createObjectURL(this.imageFile) : undefined;
		return (
			<div className="fa-file-upload">
				<>
					{
						this.props.title &&
						<div className="slds-p-bottom_small" style={{ fontWeight: 700 }}>{this.props.title}</div>
					}
					{
						!!imageUrl &&
						<div style={{ width: "100%" }}>
							<img
								className="slds-align_absolute-center"
								style={{ maxWidth: "100%" }}
								src={imageUrl}
								alt="Neues Foto"
							/>
						</div>
					}
					{
						!imageUrl && !this.props.documentContentUrl &&
						<div style={{ width: "100%", minHeight: minHeight }} className="fa-file-upload-empty">
						</div>
					}
					{
						!imageUrl && !!this.props.documentContentUrl &&
						<div style={{ width: "100%" }} >
							<img
								className="slds-align_absolute-center"
								style={{ maxWidth: "100%" }}
								src={this.props.documentContentUrl}
								alt="Aktuelles Foto"
							/>
						</div>
					}
				</>
				<div className="fa-file-upload-buttons">
					<div className="slds-grid slds-wrap slds-m-top_small">
						<div className="slds-col slds-size_1-of-1">
							<div className="slds-form" role="list">
								<FieldGroup>
									<FieldRow>
										{
											!imageUrl &&
											<div className="slds-file-selector slds-file-selector_files">
												<div className="slds-file-selector__dropzone">
													<input
														type="file"
														className="slds-file-selector__input slds-assistive-text"
														accept={this.props.supportedContentTypes}
														onChange={this.readFile}
														id={"file-upload-file" + this.props.title}
													/>
													<label className="slds-file-selector__body" htmlFor={"file-upload-file" + this.props.title}>
														<span className="slds-file-selector__button slds-button slds-button_neutral">
															<svg className="slds-button__icon slds-button__icon_left" aria-hidden="true">
																<use xlinkHref="/assets/icons/utility-sprite/svg/symbols.svg#upload"></use>
															</svg>
															Foto auswählen ({this.props.title})
														</span>
														{/*<span className="slds-file-selector__text slds-medium-show">oder droppen</span>*/}
													</label>
												</div>
											</div>
										}
										{
											!!this.imageFile?.size &&
											<>
												<Button variant="brand" onClick={this.doUpload} className="slds-m-left_small">Upload</Button>
												<Button onClick={this.cancelUpload} className="slds-m-left_small">Cancel</Button>
											</>
										}
									</FieldRow>
								</FieldGroup>
							</div>
						</div>
					</div>
				</div>
			</div >
		);
	}

	private readFile = (e: React.ChangeEvent<HTMLInputElement>) => {
		console.log("readFile", this.props.title, this.props.documentId);
		e.preventDefault();
		if (e.target?.files?.[0]) {
			this.onFileChange(e.target?.files?.[0]);
		} else {
			this.onFileChange(undefined);
		}
	}

	private cancelUpload = (e: React.MouseEvent<HTMLButtonElement>) => {
		this.onFileChange(undefined);
	}

	private onFileChange = (f: File | undefined) => {
		console.log("onFileChange", this.props.title, this.props.documentId, f);
		this.imageFileSeqNr += 1;
		this.imageFile = f;
	}

	private doUpload = async () => {
		if (!this.imageFile) {
			return;
		}
		try {
			this.isUploading = true;
			this.hasUploadError = false;
			const data = new FormData();
			data.append("file", this.imageFile);
			const url = Config.getRestUrl("dms", "documents/" + this.props.documentId + "/content");
			await API.post(url, data);
			this.imageFile = undefined;
			this.props.afterUpload();
		} catch (e) {
			this.hasUploadError = true;
		} finally {
			this.isUploading = false;
		}
	}

}

const UploadAlert = (props: { clearError: () => void }) => {
	return <div className="slds-notify slds-notify_alert slds-alert_error" role="alert">
		<span className="slds-assistive-text">error</span>
		<span className="slds-icon_container slds-icon-utility-error slds-m-right_x-small">
			<svg className="slds-icon slds-icon_x-small" aria-hidden="true">
				<use xlinkHref="/assets/icons/utility-sprite/svg/symbols.svg#error"></use>
			</svg>
		</span>
		<h2>Upload nicht erfolgreich (allenfalls ist Bild zu gross, max. 4MB)!</h2>
		<div className="slds-notify__close">
			<button className="slds-button slds-button_icon slds-button_icon-small slds-button_icon-inverse" title="Close" onClick={props.clearError}>
				<svg className="slds-button__icon" aria-hidden="true">
					<use xlinkHref="/assets/icons/utility-sprite/svg/symbols.svg#close"></use>
				</svg>
				<span className="slds-assistive-text">Close</span>
			</button>
		</div>
	</div>
};
