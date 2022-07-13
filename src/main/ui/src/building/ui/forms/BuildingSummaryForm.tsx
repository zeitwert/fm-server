
import { Button, Card, Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow } from "@zeitwert/ui-forms";
import { API, Building, Config } from "@zeitwert/ui-model";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";

export interface BuildingSummaryFormProps {
	building: Building;
	afterSave: () => void;
}

@observer
export default class BuildingSummaryForm extends React.Component<BuildingSummaryFormProps> {

	@observable imageFile: File | undefined;
	@observable isUploading: boolean = false;
	@observable hasUploadError: boolean = false;

	constructor(props: BuildingSummaryFormProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const { building } = this.props;
		return (
			<div>
				<Tabs variant="scoped">
					<TabsPanel key="cover" label="Cover">
						{
							this.isUploading &&
							<Spinner variant="brand" size="large" />
						}
						{
							this.hasUploadError &&
							<UploadAlert clearError={() => this.hasUploadError = false} />
						}
						<CoverUploadForm
							building={building}
							imageFile={this.imageFile}
							onFileChange={this.onFileChange}
							onUpload={this.uploadFile}
						/>
					</TabsPanel>
					<TabsPanel key="location" label="Lage">
						<div>
							<img
								className="slds-align_absolute-center"
								style={{ width: "100%" }}
								src={building.locationUrl}
								alt="Lageplan"
							/>
						</div>
					</TabsPanel>
				</Tabs>
			</div>
		);
	}

	private onFileChange = (f: File | undefined) => {
		this.imageFile = f;
	}

	private uploadFile = async () => {
		if (!this.imageFile) {
			return;
		}
		try {
			this.isUploading = true;
			this.hasUploadError = false;
			const data = new FormData();
			data.append("file", this.imageFile);
			const url = Config.getRestUrl("building", "buildings/" + this.props.building.id + "/coverFoto");
			await API.post(url, data);
			this.imageFile = undefined;
			this.props.afterSave();
		} catch (e) {
			this.hasUploadError = true;
		} finally {
			this.isUploading = false;
		}
	}

}


export interface CoverUploadFormProps {
	building: Building;
	imageFile: File | undefined;
	onFileChange: (f: File | undefined) => void;
	onUpload: () => void;
}

@observer
class CoverUploadForm extends React.Component<CoverUploadFormProps> {

	render() {
		const { building } = this.props;
		const coverFoto = building.coverFoto;
		const imageUrl = this.props.imageFile ? URL.createObjectURL(this.props.imageFile) : undefined;
		if (!coverFoto?.id) {
			return (
				<div style={{ width: "100%", minHeight: "200px", backgroundColor: "#dddddd", padding: "10px" }} >
					<p>Das Gebäude hat noch kein Coverfoto angelegt (aus DB Migration).</p>
					<p>&nbsp;</p>
					<p><b>Bitte Immobilie bearbeiten und speichern.</b></p>
				</div>
			);
		}
		return (
			<>
				{
					!!imageUrl &&
					<div style={{ width: "100%", minHeight: "200px" }} >
						<img
							className="slds-align_absolute-center"
							style={{ width: "100%" }}
							src={imageUrl}
							alt="Neues Coverfoto"
						/>
					</div>
				}
				{
					!imageUrl &&
					<div style={{ width: "100%", minHeight: "200px" }} >
						<img
							className="slds-align_absolute-center"
							style={{ width: "100%" }}
							src={building.coverFotoUrl}
							alt="Coverfoto"
						/>
					</div>
				}
				<div className="slds-grid slds-wrap slds-m-top_small">
					<div className="slds-col slds-size_1-of-1">
						<Card hasNoHeader>
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup>
										<FieldRow>
											{
												!imageUrl &&
												<div className="slds-file-selector slds-file-selector_files">
													<div className="slds-file-selector__dropzone">
														<input type="file" className="slds-file-selector__input slds-assistive-text" accept={coverFoto?.supportedContentTypes} onChange={this.readFile} id="file-upload-input-115" aria-labelledby="file-selector-primary-label-113 file-selector-secondary-label114" />
														<label className="slds-file-selector__body" htmlFor="file-upload-input-115" id="file-selector-secondary-label114">
															<span className="slds-file-selector__button slds-button slds-button_neutral">
																<svg className="slds-button__icon slds-button__icon_left" aria-hidden="true">
																	<use xlinkHref="/assets/icons/utility-sprite/svg/symbols.svg#upload"></use>
																</svg>Foto wählen</span>
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
						</Card>
					</div>
				</div>
			</>
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
