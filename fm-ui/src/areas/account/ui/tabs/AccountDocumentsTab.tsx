
import { Tabs, TabsPanel } from "@salesforce/design-system-react";
import { Account } from "@zeitwert/ui-model";
import AppBanner from "app/ui/AppBannerSvg";
import { Canvg, presets } from "canvg";
import FotoUploadForm from "lib/item/ui/tab/FotoUploadForm";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";

export interface AccountDocumentsTabProps {
	account: Account;
	afterSave: () => void;
}

const preset = presets.offscreen();

@observer
export default class AccountDocumentsTab extends React.Component<AccountDocumentsTabProps> {

	@observable hasBanner: boolean = false;
	@observable bannerUrl: string | undefined;

	constructor(props: AccountDocumentsTabProps) {
		super(props);
		makeObservable(this);
	}

	componentDidMount(): void {
		this.afterLogoUpload();
	}

	render() {
		const { account } = this.props;
		return (
			<div>
				<Tabs variant="scoped">
					<TabsPanel key="images" label="Bilder">
						<FotoUploadForm
							title="Logo"
							documentId={account.logo?.id}
							documentContentUrl={account.hasLogo ? account.logoUrl : undefined}
							supportedContentTypes={account.logo?.supportedContentTypes}
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
		const { account } = this.props;
		if (f != null && !!f.size) {
			const logoUrl = URL.createObjectURL(f);
			this.createBannerUrl(true, logoUrl, account.caption!, account.tenant?.name!);
		} else {
			return this.afterLogoUpload();
		}
	}

	private afterLogoUpload = async () => {
		const { account } = this.props;
		this.createBannerUrl(account.hasLogo, account.logoUrl!, account.caption!, account.tenant?.name!);
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
