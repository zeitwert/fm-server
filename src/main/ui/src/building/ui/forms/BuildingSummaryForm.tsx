
import { Button, Card, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow } from "@zeitwert/ui-forms";
import { API, Building, Config } from "@zeitwert/ui-model";
import { AppCtx } from "App";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

export interface BuildingSummaryFormProps {
	building: Building;
}

@observer
export default class BuildingSummaryForm extends React.Component<BuildingSummaryFormProps> {

	@observable imageFile: File | undefined;

	constructor(props: CoverUploadFormProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const { building: item } = this.props;
		return (
			<div>
				<Tabs variant="scoped">
					<TabsPanel key="cover" label="Cover">
						<CoverUploadForm building={item} imageFile={this.imageFile} onFileChange={(f) => this.imageFile = f} />
					</TabsPanel>
					<TabsPanel key="location" label="Lage">
						<div>
							<img
								className="slds-align_absolute-center"
								style={{ width: "100%" }}
								src={Config.getLocationUrl("building", "buildings/" + item.id)}
								alt="Lageplan"
							/>
						</div>
					</TabsPanel>
				</Tabs>
			</div>
		);
	}

}


export interface CoverUploadFormProps {
	building: Building;
	imageFile: File | undefined;
	onFileChange: (f: File | undefined) => void;
}

@observer
@inject("appStore", "session", "showAlert", "showToast")
class CoverUploadForm extends React.Component<CoverUploadFormProps> {

	get ctx() {
		return this.props as any as AppCtx;
	}

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
		const url = Config.getRestUrl("dms", "documents/" + coverFoto?.id + "/content");
		return (
			<>
				{
					coverFoto?.contentType?.id && !imageUrl &&
					<div style={{ width: "100%", minHeight: "200px" }} >
						<img
							className="slds-align_absolute-center"
							style={{ width: "100%" }}
							src={url}
							alt="Coverfoto"
						/>
					</div>
				}
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
				<div className="slds-grid slds-wrap slds-m-top_small">
					<div className="slds-col slds-size_1-of-1">
						<Card hasNoHeader>
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup>
										<FieldRow>
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
											{/*<input type="file" accept={item.coverFoto?.supportedContentTypes} onChange={this.readFile} size={8} />*/}
											{
												!!this.props.imageFile &&
												<>
													<Button variant="brand" onClick={this.uploadFile} className="slds-m-left_small">Upload</Button>
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

	private uploadFile = async (e: React.MouseEvent<HTMLButtonElement>) => {
		if (this.props.imageFile) {
			const data = new FormData();
			data.append("file", this.props.imageFile);
			const url = Config.getRestUrl("dms", "documents/" + this.props.building.coverFoto?.id + "/content");
			try {
				await API.post(url, data);
				this.ctx.showToast("success", "Foto gespeichert");
			} catch (error: any) {
				this.ctx.showAlert(
					"error",
					"Could not store foto: " + (error.detail ? error.detail : error.title ? error.title : error)
				);
			}
		}
	}

	private cancelUpload = (e: React.MouseEvent<HTMLButtonElement>) => {
		this.props.onFileChange(undefined);
	}

}


/*


				<div className="slds-popover__header">
					<header className="slds-media slds-media_center slds-m-bottom_small">
						<span className="slds-icon_container slds-icon-standard-account slds-media__figure">
							<svg className="slds-icon slds-icon_small" aria-hidden="true">
								<use xlinkHref="/assets/icons/standard-sprite/svg/symbols.svg#store"></use>
							</svg>
						</span>
						<div className="slds-media__body">
							<h2 className="slds-text-heading_medium slds-hyphenate" id="panel-heading-id">
								<a href="/#">{item.name}</a>
							</h2>
						</div>
					</header>
					<footer className="slds-grid slds-wrap slds-grid_pull-padded">
						<div className="slds-p-horizontal_small slds-size_1-of-2 slds-p-bottom_x-small">
							<dl>
								<dt>
									<p className="slds-popover_panel__label slds-truncate" title="Billing Address">Address</p>
								</dt>
								<dd>
									<p className="slds-truncate" title="3500 Deer Creek Rd.">{item.street}</p>
									<p className="slds-truncate" title="Palo Alto, CA 94304">{item.zip} {item.city}</p>
								</dd>
							</dl>
						</div>
						<div className="slds-p-horizontal_small slds-size_1-of-2 slds-p-bottom_x-small">
							<dl>
								<dt>
									<p className="slds-popover_panel__label slds-truncate" title="Account">Account</p>
								</dt>
								<dd>
									<a href="/#">{item.account?.name}</a>
								</dd>
							</dl>
						</div>
						<div className="slds-p-horizontal_small slds-size_1-of-2 slds-p-bottom_x-small">
							<dl>
								<dt>
									<p className="slds-popover_panel__label slds-truncate" title="Website">Website</p>
								</dt>
								<dd>
									<a href="/#">www.zeitwert.io</a>
								</dd>
							</dl>
						</div>
						<div className="slds-p-horizontal_small slds-size_1-of-2 slds-p-bottom_x-small">
							<dl>
								<dt>
									<p className="slds-popover_panel__label slds-truncate" title="Account Owner">Account Owner</p>
								</dt>
								<dd>
									<a href="/#">{item.owner.name}</a>
								</dd>
							</dl>
						</div>
						<div className="slds-p-horizontal_small slds-size_1-of-2 slds-p-bottom_x-small">
							<dl>
								<dt>
									<p className="slds-popover_panel__label slds-truncate" title="Website">Document</p>
								</dt>
								<dd>
									<a href="/#">{item.coverFoto?.contentKind?.name}</a>
								</dd>
							</dl>
						</div>
						<div className="slds-p-horizontal_small slds-size_1-of-2 slds-p-bottom_x-small">
							<dl>
								<dt>
									<p className="slds-popover_panel__label slds-truncate" title="Account Owner">Content</p>
								</dt>
								<dd>
									<a href="/#">{item.coverFoto?.contentType?.name}</a>
								</dd>
							</dl>
						</div>
					</footer>
				</div>

				<div className="slds-popover__body">
					<dl className="slds-popover__body-list">
						<dt className="slds-m-bottom_small">
							<div className="slds-media slds-media_center">
								<div className="slds-media__figure">
									<span className="slds-icon_container slds-icon-standard-opportunity">
										<svg className="slds-icon slds-icon_small" aria-hidden="true">
											<use xlinkHref="/assets/icons/standard-sprite/svg/symbols.svg#opportunity"></use>
										</svg>
										<span className="slds-assistive-text">Opportunities</span>
									</span>
								</div>
								<div className="slds-media__body">
									<p className="slds-text-heading_small slds-hyphenate">Opportunities (2+)</p>
								</div>
							</div>
						</dt>
						<dd className="slds-m-top_x-small">
							<p className="slds-truncate" title="Tesla - Mule ESB">
								<a href="/#">Tesla - Mule ESB</a>
							</p>
							<dl className="slds-list_horizontal slds-wrap slds-text-body_small">
								<dt className="slds-item_label slds-text-color_weak slds-truncate" title="Value">Value</dt>
								<dd className="slds-item_detail slds-text-color_weak slds-truncate" title="$500,000">$500,000</dd>
								<dt className="slds-item_label slds-text-color_weak slds-truncate" title="Close Date">Close Date</dt>
								<dd className="slds-item_detail slds-text-color_weak slds-truncate" title="Dec 15, 2015">Dec 15, 2015</dd>
							</dl>
						</dd>
						<dd className="slds-m-top_x-small">
							<p className="slds-truncate" title="Tesla - Anypoint Studios">
								<a href="/#">Tesla - Anypoint Studios</a>
							</p>
							<dl className="slds-list_horizontal slds-wrap slds-text-body_small">
								<dt className="slds-item_label slds-text-color_weak slds-truncate" title="Value">Value</dt>
								<dd className="slds-item_detail slds-text-color_weak slds-truncate" title="$60,000">$60,000</dd>
								<dt className="slds-item_label slds-text-color_weak slds-truncate" title="Close Date">Close Date</dt>
								<dd className="slds-item_detail slds-text-color_weak slds-truncate" title="Jan 15, 2016">Jan 15, 2016</dd>
							</dl>
						</dd>
						<dd className="slds-m-top_x-small slds-text-align_right">
							<a href="/#" title="View all Opportunities">View All</a>
						</dd>
					</dl>
					<dl className="slds-popover__body-list">
						<dt className="slds-m-bottom_small">
							<div className="slds-media slds-media_center">
								<div className="slds-media__figure">
									<span className="slds-icon_container slds-icon-standard-case">
										<svg className="slds-icon slds-icon_small" aria-hidden="true">
											<use xlinkHref="/assets/icons/standard-sprite/svg/symbols.svg#case"></use>
										</svg>
										<span className="slds-assistive-text">Cases</span>
									</span>
								</div>
								<div className="slds-media__body">
									<p className="slds-text-heading_small slds-hyphenate">Cases (1)</p>
								</div>
							</div>
						</dt>
						<dd className="slds-m-top_x-small">
							<p className="slds-truncate" title="Tesla - Anypoint Studios">
								<a href="/#">Tesla - Anypoint Studios</a>
							</p>
							<dl className="slds-list_horizontal slds-wrap slds-text-body_small">
								<dt className="slds-item_label slds-text-color_weak slds-truncate" title="Value">Value</dt>
								<dd className="slds-item_detail slds-text-color_weak slds-truncate" title="$60,000">$60,000</dd>
								<dt className="slds-item_label slds-text-color_weak slds-truncate" title="Close Date">Close Date</dt>
								<dd className="slds-item_detail slds-text-color_weak slds-truncate" title="Jan 15, 2016">Jan 15, 2016</dd>
							</dl>
						</dd>
						<dd className="slds-m-top_x-small slds-text-align_right">
							<a href="/#" title="View all Opportunities">View All</a>
						</dd>
					</dl>
				</div>


*/