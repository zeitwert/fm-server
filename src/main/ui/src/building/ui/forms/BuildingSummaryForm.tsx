
import { Tabs, TabsPanel } from "@salesforce/design-system-react";
import { Building } from "@zeitwert/ui-model";
import FotoUploadForm from "dms/ui/forms/FotoUploadForm";
import { observer } from "mobx-react";
import React from "react";

export interface BuildingSummaryFormProps {
	building: Building;
	afterSave: () => void;
}

@observer
export default class BuildingSummaryForm extends React.Component<BuildingSummaryFormProps> {

	render() {
		const { building } = this.props;
		return (
			<div>
				<Tabs variant="scoped">
					<TabsPanel key="cover" label="Cover">
						<FotoUploadForm
							title="CoverFoto"
							documentId={building.coverFoto?.id}
							documentContentUrl={building.hasCoverFoto ? building.coverFotoUrl : undefined}
							supportedContentTypes={building.coverFoto?.supportedContentTypes}
							afterUpload={() => this.props.afterSave && this.props.afterSave()}
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

}

