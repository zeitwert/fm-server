
import { Tabs, TabsPanel } from "@salesforce/design-system-react";
import { Account } from "@zeitwert/ui-model";
import FotoUploadForm from "dms/ui/forms/FotoUploadForm";
import { observer } from "mobx-react";
import React from "react";

export interface AccountSummaryFormProps {
	account: Account;
	afterSave: () => void;
}

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
							afterUpload={() => this.props.afterSave && this.props.afterSave()}
						/>
						<div className="slds-p-top_small" />
						<FotoUploadForm
							title="Banner"
							documentId={account.banner?.id}
							documentContentUrl={account.hasBanner ? account.bannerUrl : undefined}
							supportedContentTypes={account.banner?.supportedContentTypes}
							afterUpload={() => this.props.afterSave && this.props.afterSave()}
						/>
					</TabsPanel>
				</Tabs>
			</div>
		);
	}

}
