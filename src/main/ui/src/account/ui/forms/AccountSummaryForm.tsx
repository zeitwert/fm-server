
import { Tabs, TabsPanel } from "@salesforce/design-system-react";
import { Account, API, Config } from "@zeitwert/ui-model";
import { Canvg, presets } from "canvg";
import FotoUploadForm from "dms/ui/forms/FotoUploadForm";
import { observer } from "mobx-react";
import React from "react";
import { SvgHeader as AccountSvgHeader } from "../../../frame/ui/SvgAccountHeader";

export interface AccountSummaryFormProps {
	account: Account;
	afterSave: () => void;
}

const preset = presets.offscreen();

@observer
export default class TenantSummaryForm extends React.Component<AccountSummaryFormProps> {

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
							afterUpload={this.afterLogoUpload}
						/>
						<div className="slds-p-top_small" />
						<FotoUploadForm
							title="Banner"
							documentId={account.banner?.id}
							documentContentUrl={account.hasBanner ? account.bannerUrl : undefined}
							supportedContentTypes=""
							afterUpload={async () => this.props.afterSave && this.props.afterSave()}
						/>
					</TabsPanel>
				</Tabs>
			</div>
		);
	}

	private afterLogoUpload = async () => {
		console.log("afterLogoUpload", this.props.account.bannerUrl);
		const blob = await this.toPng({ width: 300, height: 50, svg: this.props.account.bannerUrl });
		console.log("afterLogoUpload.1");
		const imageFile = new File([blob], "banner.png");
		console.log("afterLogoUpload.2");
		const data = new FormData();
		console.log("afterLogoUpload.3");
		data.append("file", imageFile);
		console.log("afterLogoUpload.4");
		const url = Config.getRestUrl("dms", "documents/" + this.props.account.banner?.id + "/content");
		console.log("afterLogoUpload.5");
		await API.post(url, data);
		console.log("afterLogoUpload.6");
		this.props.afterSave && this.props.afterSave();
		console.log("afterLogoUpload.7");
	}

	private toPng = async (data: any): Promise<Blob> => {
		console.log("toPng", data);
		const { width, height } = data;
		const { account } = this.props;
		const svggg = AccountSvgHeader
			.replace("{logo}", Config.getRestUrl("account", "accounts/" + account.id + "/logo"))
			.replace("{account}", account.caption!)
			.replace("{tenant}", account.tenant!.name);
		console.log("toPng.1");
		const canvas = new OffscreenCanvas(width, height);
		const ctx = canvas.getContext("2d")!;
		console.log("toPng.2");
		const v = await Canvg.from(ctx, svggg, preset);
		console.log("toPng.3a");
		await timeout(1000);
		console.log("toPng.3b");
		await v.render(); // render only first frame, ignoring animations and mouse.
		console.log("toPng.4");
		return await canvas.convertToBlob();
		// console.log("blob", blob);
		// const pngUrl = URL.createObjectURL(blob);
		// console.log("url", pngUrl);
		// return pngUrl
	}

}

function timeout(ms: number) {
	return new Promise(resolve => setTimeout(resolve, ms));
}