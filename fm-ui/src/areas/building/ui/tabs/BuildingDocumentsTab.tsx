
import { Tabs, TabsPanel } from "@salesforce/design-system-react";
import { API, Building } from "@zeitwert/ui-model";
import FotoUploadForm from "lib/item/ui/tab/FotoUploadForm";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";

export interface BuildingDocumentsTabProps {
	building: Building;
	afterSave: () => void;
}

@observer
export default class BuildingDocumentsTab extends React.Component<BuildingDocumentsTabProps> {

	@observable
	objectURL: string | undefined;

	constructor(props: BuildingDocumentsTabProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount(): Promise<void> {
		const { building } = this.props;
		const res = await API.get(building.locationUrl!, { responseType: "blob" });
		const blob = await res.data;
		this.objectURL = URL.createObjectURL(blob);
	}

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
							afterUpload={async () => this.props.afterSave && this.props.afterSave()}
						/>
					</TabsPanel>
					<TabsPanel key="location" label="Lage">
						<div>
							<img
								className="slds-align_absolute-center"
								style={{ width: "100%" }}
								src={this.objectURL}
								alt="Lageplan"
							/>
						</div>
					</TabsPanel>
				</Tabs>
			</div>
		);
	}

}

