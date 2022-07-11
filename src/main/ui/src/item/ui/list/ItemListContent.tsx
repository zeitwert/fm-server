import { KANBAN_API, LayoutType, Template } from "@zeitwert/ui-model";
import { ReportViewer } from "@zeitwert/ui-slds/report/ReportViewer";
import { AppCtx } from "frame/App";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

// const GOOGLE_API_KEY = "AIzaSyBQF6Fi_Z0tZxVh5Eqzfx2m7hK3n718jsI";
// const MAP_QUEST_API = "https://open.mapquestapi.com/geocoding/v1/address?key=2fZ97ASJF6Dgrz3KqToysJHRGDJf7kOB&location=";

interface ItemListContentProps {
	template?: Template;
	reportData: any;
	reportTemplates?: any;
	hasMap?: boolean;
	showMap?: boolean;
	onClick?: (itemId: string) => void;
}

@inject("showToast")
@observer
export default class ItemListContent extends React.Component<ItemListContentProps> {

	// @observable
	// private buildings: BuildingInfo[] = [];

	// @observable
	// private currentBuildingId: string | undefined = undefined;

	@observable
	private reportData: any = {};

	constructor(props: ItemListContentProps) {
		super(props);
		// this.buildings = this.props.reportData?.data || [];
		this.reportData = Object.assign({}, this.props.reportData);
		makeObservable(this);
	}

	// async componentDidMount() {
	// 	if (this.props.hasMap && this.reportData?.data) {
	// 		this.buildings = await Promise.all(this.buildings.map((d: any) => this.geocode(d)));
	// 		this.reportData.data = this.buildings;
	// 	}
	// }

	get ctx() {
		return this.props as any as AppCtx;
	}

	render() {
		const layout = this.props.template?.layout;
		if (!layout) {
			return <></>;
		}
		let options = {};
		switch (layout.layoutType) {
			default:
			case LayoutType.Line:
				options = {
					options: [
						{
							id: 0,
							label: "Add to Group",
							value: "1"
						},
						{
							id: 1,
							label: "Publish",
							value: "2"
						}
					]
				};
				break;
			case LayoutType.Kanban:
				options = {
					onCardMoved: this.onCardMoved,
					cardActions: [
						{
							name: "Edit",
							action: (itemId: string) => Promise.resolve(console.log("Edit card " + itemId))
						},
						{
							name: "Delete",
							action: (itemId: string) => Promise.resolve(console.log("Delete card " + itemId))
						}
					]
				};

				break;
		}
		if (false && this.props.showMap) {
			// return (
			// 	<Grid isVertical={false} style={{ border: "none", height: "100%", paddingTop: "0", backgroundColor: "red" }}>
			// 		<Col cols={1} totalCols={3} style={{ border: "none", height: "100%", paddingTop: "0", backgroundColor: "yellow" }}>
			// 			<ReportViewer
			// 				layout={layout}
			// 				data={this.reportData}
			// 				templates={this.props.reportTemplates}
			// 				maxColumns={2}
			// 				options={options}
			// 				onMouseEnter={(id: string) => { this.currentBuildingId = id; }}
			// 				onMouseLeave={(id: string) => { this.currentBuildingId = undefined; }}
			// 				onClick={(id: string) => { this.props.onClick?.(this.getItem(id)) }}
			// 			/>
			// 		</Col>
			// 		<Col cols={2} totalCols={3} style={{ border: "none", height: "100%", paddingTop: "0", backgroundColor: "green" }}>
			// 			<Wrapper apiKey={GOOGLE_API_KEY} render={this.renderStatus}>
			// 				<ItemListMap
			// 					buildings={this.buildings}
			// 					currentBuildingId={this.currentBuildingId}
			// 					onMouseEnter={(id: string) => { this.currentBuildingId = id; this.markTableRowElement(id, "fa-hover"); }}
			// 					onMouseLeave={(id: string) => { this.currentBuildingId = undefined; this.unmarkTableRowElement(id, "fa-hover"); }}
			// 				/>
			// 			</Wrapper>
			// 		</Col>
			// 	</Grid>
			// );
		} else {
			return (
				<ReportViewer
					layout={layout}
					data={this.reportData}
					templates={this.props.reportTemplates}
					options={options}
					onClick={this.props.onClick}
				/>
			);
		}
	}

	// private async geocode(d: any): Promise<BuildingInfo> {
	// 	const result = await axios.get(MAP_QUEST_API + d.address + " CH");
	// 	d.hasPosition = !!result.data.results[0].locations[0].adminArea2 || !!result.data.results[0].locations[0].adminArea3 || !!result.data.results[0].locations[0].adminArea4;
	// 	d.position = d.hasPosition ? result.data.results[0].locations[0].latLng : undefined;
	// 	return d;
	// 	// const result = await this.geocoder.geocode(request);
	// 	// const { results } = result;
	// 	// return results[0].geometry.location.toJSON();
	// }

	// private getItem(itemId: string): any {
	// 	if (!itemId) {
	// 		return undefined;
	// 	}
	// 	return this.reportData.data.find((d: any) => d.id === itemId);
	// }

	// private markTableRowElement(id: string, cssClass: string) {
	// 	const tr = this.getTableRowElement(id);
	// 	tr?.classList.add(cssClass);
	// }

	// private unmarkTableRowElement(id: string, cssClass: string) {
	// 	const tr = this.getTableRowElement(id);
	// 	tr?.classList.remove(cssClass);
	// }

	// private getTableRowElement(id: string): Element | undefined {
	// 	const input: Element | null = document.getElementById(PREFIX + id + POSTFIX);
	// 	if (input) {
	// 		const tr = input.parentElement?.parentElement?.parentElement?.parentElement?.parentElement;
	// 		return tr ? tr : undefined;
	// 	}
	// }

	private onCardMoved = async (itemId: string, id: string) => {
		const layout = this.props.template!.layout!;
		const { docType, groupBy, modifyUrl } = layout.layout;
		const field = groupBy.serverFieldName;
		try {
			await KANBAN_API.updateItem(modifyUrl, docType, itemId, field, id);
			this.ctx.showToast("success", docType.charAt(0).toUpperCase() + docType.slice(1) + " stored.");
		} catch (err) {
			this.ctx.showToast("warning", "Error while modifying " + docType + "!");
		}
	};

	// private renderStatus = (status: Status): React.ReactElement => {
	// 	if (status === Status.LOADING) return <h3>{status} ..</h3>;
	// 	if (status === Status.FAILURE) return <h3>{status} ...</h3>;
	// 	return <></>;
	// };

}
