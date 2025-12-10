
import { Tabs, TabsPanel } from "@salesforce/design-system-react";
import { User } from "@zeitwert/ui-model";
import FotoUploadForm from "lib/item/ui/tab/FotoUploadForm";
import { observer } from "mobx-react";
import React from "react";

export interface UserDocumentsTabProps {
	user: User;
	afterSave: () => void;
}

@observer
export default class UserDocumentsTab extends React.Component<UserDocumentsTabProps> {

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
