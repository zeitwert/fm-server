
import Button from "@salesforce/design-system-react/components/button";
import ButtonGroup from "@salesforce/design-system-react/components/button-group";
import Spinner from "@salesforce/design-system-react/components/spinner";
import Tabs from "@salesforce/design-system-react/components/tabs";
import TabsPanel from "@salesforce/design-system-react/components/tabs/panel";
import { Account, Building, BuildingStore, BuildingStoreModel, Config, EntityType } from "@zeitwert/ui-model";
import { AppCtx } from "App";
import { RouteComponentProps, withRouter } from "frame/app/withRouter";
import ItemEditor from "item/ui/ItemEditor";
import { ItemGrid, ItemLeftPart, ItemRightPart } from "item/ui/ItemGrid";
import ItemHeader, { HeaderDetail } from "item/ui/ItemHeader";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import TabProjection from "projection/ui/TabProjection";
import React from "react";
import BuildingRatingForm from "./forms/BuildingRatingForm";
import BuildingStaticDataForm from "./forms/BuildingStaticDataForm";

enum TAB {
	OVERVIEW = 0,
	RATING = 1,
	EVALUATION = 2
}

@inject("appStore", "session", "showAlert", "showToast")
@observer
class BuildingPage extends React.Component<RouteComponentProps> {

	@observable activeLeftTabId = TAB.OVERVIEW;
	@observable buildingStore: BuildingStore = BuildingStoreModel.create({});
	@observable isLoaded = false;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: RouteComponentProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		await this.buildingStore.load(this.props.params.buildingId!);
		this.isLoaded = true;
	}

	async componentDidUpdate(prevProps: RouteComponentProps) {
		if (this.props.params.buildingId !== prevProps.params.buildingId) {
			await this.buildingStore.load(this.props.params.buildingId!);
			this.isLoaded = true;
		}
	}

	render() {
		if (!this.isLoaded) {
			return <Spinner variant="brand" size="large" />;
		}
		const building = this.buildingStore.building!;
		const isFullWidth = [TAB.RATING, TAB.EVALUATION].indexOf(this.activeLeftTabId) >= 0;
		return (
			<>
				<ItemHeader
					store={this.buildingStore}
					details={this.getHeaderDetails(building)}
					customActions={this.getHeaderActions()}
				/>
				<ItemGrid>
					<ItemLeftPart isFullWidth={isFullWidth}>
						<ItemEditor
							store={this.buildingStore}
							entityType={EntityType.BUILDING}
							showEditButtons={this.activeLeftTabId === TAB.OVERVIEW || this.activeLeftTabId === TAB.RATING}
							onOpen={this.openEditor}
							onCancel={this.cancelEditor}
							onClose={this.closeEditor}
						>
							<Tabs
								className="full-height"
								selectedIndex={this.activeLeftTabId}
								onSelect={(tabId) => (this.activeLeftTabId = tabId)}
							>
								<TabsPanel label="Stammdaten">
									{this.activeLeftTabId === TAB.OVERVIEW && <BuildingStaticDataForm store={this.buildingStore} />}
								</TabsPanel>
								<TabsPanel label="Bewertung">
									{this.activeLeftTabId === TAB.RATING && <BuildingRatingForm store={this.buildingStore} />}
								</TabsPanel>
								<TabsPanel label="Auswertung">
									{this.activeLeftTabId === TAB.EVALUATION && <TabProjection url={"buildings/" + this.buildingStore.building?.id} />}
								</TabsPanel>
							</Tabs>
						</ItemEditor>
					</ItemLeftPart>
					<>
						{
							!isFullWidth &&
							<ItemRightPart store={this.buildingStore} account={building.account as Account} />
						}
					</>
				</ItemGrid>
			</>
		);
	}

	private getHeaderDetails(building: Building): HeaderDetail[] {
		return [];
		// return [
		// 	{
		// 		label: "Owner",
		// 		content: building.owner!.caption,
		// 		icon: (
		// 			<Avatar
		// 				variant="user"
		// 				size="small"
		// 				imgSrc={building.owner!.picture}
		// 				imgAlt={building.owner!.caption}
		// 				label={building.owner!.caption}
		// 			/>
		// 		),
		// 		link: "/user/" + building.owner!.id
		// 	},
		// 	{
		// 		label: "Account",
		// 		content: building.account?.caption,
		// 		icon: <Icon category="standard" name="account" size="small" />,
		// 		link: "/account/" + building.account?.id
		// 	},
		// 	{ label: "Address", content: `${building.street} ${building.zip} ${building.city}` }
		// ];
	}

	private getHeaderActions() {
		return (
			<>
				<ButtonGroup variant="list">
					<Button onClick={() => { window.location.href = Config.getTransferUrl("building", "buildings/" + this.props.params.buildingId!); }}>Export</Button>
				</ButtonGroup>
			</>
		);
	}

	private openEditor = () => {
		this.buildingStore.edit();
	};

	private cancelEditor = async () => {
		await this.buildingStore.cancel();
	};

	private closeEditor = async () => {
		try {
			const item = await this.buildingStore.store();
			this.ctx.showToast("success", `Building stored`);
			return item;
		} catch (error: any) {
			this.ctx.showAlert(
				"error",
				"Could not store Building: " + (error.detail ? error.detail : error.title ? error.title : error)
			);
		}
	};

}

export default withRouter(BuildingPage);
