
import { Spinner, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { API, Building, Config } from "@zeitwert/ui-model";
import FotoUploadForm from "dms/ui/forms/FotoUploadForm";
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
					<TabsPanel key="avatar" label="Avatar">
						{
							this.isUploading &&
							<Spinner variant="brand" size="large" />
						}
						{
							this.hasUploadError &&
							<UploadAlert clearError={() => this.hasUploadError = false} />
						}
						<FotoUploadForm
							documentId={building.coverFoto?.id}
							documentContentUrl={building.hasCoverFoto ? building.coverFotoUrl : undefined}
							supportedContentTypes={building.coverFoto?.supportedContentTypes}
							imageFile={this.imageFile}
							onFileChange={this.onFileChange}
							onUpload={this.uploadFile}
						/>
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

