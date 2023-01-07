
import { Tabs, TabsPanel } from "@salesforce/design-system-react";
import { Tenant } from "@zeitwert/ui-model";
import FotoUploadForm from "areas/document/ui/forms/FotoUploadForm";
import { Canvg, presets } from "canvg";
import AppBanner from "app/ui/AppBannerSvg";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";

export interface TenantSummaryTabProps {
	tenant: Tenant;
	afterSave: () => void;
}

const preset = presets.offscreen();

@observer
export default class TenantSummaryTab extends React.Component<TenantSummaryTabProps> {

	@observable hasBanner: boolean = false;
	@observable bannerUrl: string | undefined;

	constructor(props: TenantSummaryTabProps) {
		super(props);
		makeObservable(this);
		this.afterLogoUpload();
	}

	render() {
		const { tenant } = this.props;
		return (
			<div>
				<Tabs variant="scoped">
					<TabsPanel key="images" label="Bilder">
						<FotoUploadForm
							title="Logo"
							documentId={tenant.logo?.id}
							documentContentUrl={tenant.hasLogo ? tenant.logoUrl : undefined}
							supportedContentTypes={tenant.logo?.supportedContentTypes}
							onFileChange={this.afterLogoFileChange}
							afterUpload={this.props.afterSave}
						/>
						<div className="slds-p-top_small" />
						<div className="slds-p-bottom_small" style={{ fontWeight: 700 }}>Banner</div>
						{
							this.hasBanner &&
							<img width={300} height={50} src={this.bannerUrl} alt="Banner" />
						}
					</TabsPanel>
				</Tabs>
			</div>
		);
	}

	private afterLogoFileChange = async (f: File | undefined) => {
		const { tenant } = this.props;
		if (f != null && !!f.size) {
			const logoUrl = URL.createObjectURL(f);
			this.createBannerUrl(true, logoUrl, tenant.caption!, tenant.tenantType?.name!);
		} else {
			return this.afterLogoUpload();
		}
	}

	private afterLogoUpload = async () => {
		const { tenant } = this.props;
		this.createBannerUrl(tenant.hasLogo, tenant.logoUrl!, tenant.caption!, tenant.tenantType?.name!);
	}

	private createBannerUrl = async (hasLogo: boolean, logoUrl: string, title: string, subTitle: string): Promise<void> => {
		let svg = AppBanner
			.replace("{title}", title)
			.replace("{subTitle}", subTitle);
		if (hasLogo) {
			svg = svg.replace("{logo}", logoUrl);
		} else {
			svg = svg.replace("{logo}", "")
				.replace("<text x=\"50\"", "<text x=\"5\"")
				.replace("<text x=\"51\"", "<text x=\"5\"");
		}
		const canvas = new OffscreenCanvas(300, 50);
		const ctx = canvas.getContext("2d")!;
		const v = await Canvg.from(ctx, svg, preset);
		await v.render(); // render only first frame, ignoring animations and mouse.
		const blob = await canvas.convertToBlob();
		const bannerUrl = URL.createObjectURL(blob);
		this.hasBanner = true;
		this.bannerUrl = bannerUrl;
	}

}
