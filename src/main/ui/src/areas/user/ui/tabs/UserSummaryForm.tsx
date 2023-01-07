
import { Tabs, TabsPanel } from "@salesforce/design-system-react";
import { User } from "@zeitwert/ui-model";
import FotoUploadForm from "areas/document/ui/forms/FotoUploadForm";
import { observer } from "mobx-react";
import React from "react";

export interface UserSummaryFormProps {
	user: User;
	afterSave: () => void;
}

@observer
export default class UserSummaryForm extends React.Component<UserSummaryFormProps> {

	render() {
		const { user } = this.props;
		return (
			<div>
				<Tabs variant="scoped">
					<TabsPanel key="images" label="Bilder">
						<FotoUploadForm
							title="Avatar"
							documentId={user.avatar?.id}
							documentContentUrl={user.hasAvatar ? user.avatarUrl : undefined}
							supportedContentTypes={user.avatar?.supportedContentTypes}
							afterUpload={async () => this.props.afterSave && this.props.afterSave()}
						/>
					</TabsPanel>
				</Tabs>
			</div>
		);
	}

}
