
import { Tabs, TabsPanel } from "@salesforce/design-system-react";
import { Tenant } from "@zeitwert/ui-model";
import FotoUploadForm from "dms/ui/forms/FotoUploadForm";
import { observer } from "mobx-react";
import React from "react";

export interface TenantSummaryFormProps {
	tenant: Tenant;
	afterSave: () => void;
}

@observer
export default class TenantSummaryForm extends React.Component<TenantSummaryFormProps> {

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
							afterUpload={async () => this.props.afterSave && this.props.afterSave()}
						/>
						<div className="slds-p-top_small" />
						<FotoUploadForm
							title="Banner"
							documentId={tenant.banner?.id}
							documentContentUrl={tenant.hasBanner ? tenant.bannerUrl : undefined}
							supportedContentTypes={tenant.banner?.supportedContentTypes}
							afterUpload={async () => this.props.afterSave && this.props.afterSave()}
						/>
					</TabsPanel>
				</Tabs>
			</div>
		);
	}

}
