
import { Button } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow } from "@zeitwert/ui-forms";
import { observer } from "mobx-react";
import React from "react";

export interface FotoUploadFormProps {
	documentId: string | undefined;
	documentContentUrl: string | undefined; // GET | POST
	supportedContentTypes: string | undefined;
	imageFile: File | undefined;
	minHeight?: string;
	onFileChange: (f: File | undefined) => void;
	onUpload: () => void;
}

@observer
export default class FotoUploadForm extends React.Component<FotoUploadFormProps> {

	render() {
		const imageUrl = this.props.imageFile ? URL.createObjectURL(this.props.imageFile) : undefined;
		const minHeight = this.props.minHeight || "200px";
		if (!this.props.documentId) {
			return (
				<div style={{ width: "100%", minHeight: minHeight, backgroundColor: "#dddddd", padding: "10px" }} >
					<p>Das Foto-Dokument wurde noch nicht angelegt.</p>
					<p>&nbsp;</p>
					<p><b>Bitte Objekt bearbeiten und speichern.</b></p>
				</div>
			);
		}
		return (
			<div style={{ position: "relative" }}>
				{
					!!imageUrl &&
					<div style={{ width: "100%", minHeight: minHeight }} >
						<img
							className="slds-align_absolute-center"
							style={{ width: "100%" }}
							src={imageUrl}
							alt="Neues Foto"
						/>
					</div>
				}
				{
					!imageUrl && !this.props.documentContentUrl &&
					<div style={{ width: "100%", minHeight: minHeight, border: "2px dashed lightgrey", borderRadius: "10px" }} >
					</div>
				}
				{
					!imageUrl && !!this.props.documentContentUrl &&
					<div style={{ width: "100%", minHeight: minHeight }} >
						<img
							className="slds-align_absolute-center"
							style={{ width: "100%" }}
							src={this.props.documentContentUrl}
							alt="Aktuelles Foto"
						/>
					</div>
				}
				<div className="slds-grid slds-wrap slds-m-top_small" style={{ position: "absolute", left: "50%", marginLeft: "-60px", top: "50%", marginTop: "-20px" }}>
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
													id="file-upload-file"
												/>
												<label className="slds-file-selector__body" htmlFor="file-upload-file">
													<span className="slds-file-selector__button slds-button slds-button_neutral">
														<svg className="slds-button__icon slds-button__icon_left" aria-hidden="true">
															<use xlinkHref="/assets/icons/utility-sprite/svg/symbols.svg#upload"></use>
														</svg>
														Foto auswählen
													</span>
													{/*<span className="slds-file-selector__text slds-medium-show">oder droppen</span>*/}
												</label>
											</div>
										</div>
									}
									{
										!!this.props.imageFile &&
										<>
											<Button variant="brand" onClick={this.props.onUpload} className="slds-m-left_small">Upload</Button>
											<Button onClick={this.cancelUpload} className="slds-m-left_small">Cancel</Button>
										</>
									}
								</FieldRow>
							</FieldGroup>
						</div>
					</div>
				</div>
			</div>
		);
	}

	private readFile = (e: React.ChangeEvent<HTMLInputElement>) => {
		e.preventDefault();
		if (e.target?.files?.[0]) {
			this.props.onFileChange(e.target?.files?.[0]);
		} else {
			this.props.onFileChange(undefined);
		}
	}

	private cancelUpload = (e: React.MouseEvent<HTMLButtonElement>) => {
		this.props.onFileChange(undefined);
	}

}
