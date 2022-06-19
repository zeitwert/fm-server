import { Card, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { AggregateStore, DocStore, Enumerated, TaskStoreModel } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds/common/Grid";
import { ActivityFormTypes, ActivityPortlet } from "activity/ActivityPortlet";
import { FormParser } from "activity/forms/FormParser";
import { AppCtx } from "App";
import classNames from "classnames";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import DocumentsTab from "./tab/DocumentsTab";
import NotesTab from "./tab/NotesTab";
import StageHistoryTab from "./tab/StageHistoryTab";

enum TAB {
	ACTIVITY = 0,
	NOTES = 1,
	DOCUMENTS = 2,
	CHAT = 3,
	STAGE_HISTORY = 4
}

interface ItemGridProps {
	children?: JSX.Element[] | JSX.Element;
}

export class ItemGrid extends React.Component<ItemGridProps> {
	render() {
		return (
			<Grid
				isVertical={false}
				className="slds-is-relative slds-wrap slds-grow slds-gutters_direct-x-small slds-m-top_small"
				style={{ height: "1px" }}
			>
				{this.props.children}
			</Grid>
		);
	}
}

interface ItemLeftPartProps {
	hasItemPath?: boolean;
	isFullWidth?: boolean;
	children?: JSX.Element;
}

export class ItemLeftPart extends React.Component<ItemLeftPartProps> {
	render() {
		const { hasItemPath, isFullWidth } = this.props;
		const classes = classNames(
			"slds-size_1-of-1",
			isFullWidth ? "" : "slds-large-size_2-of-3",
			hasItemPath ? "fa-item-part" : "fa-height-100"
		);
		return (
			<Col className={classes}>
				<Card heading="" hasNoHeader className="fa-height-100" bodyClassName="slds-m-around_none">
					{this.props.children}
				</Card>
			</Col>
		);
	}
}

interface ItemRightPartProps {
	store: AggregateStore;
	areas?: Enumerated[];
	hideDocuments?: boolean;
	hideNotes?: boolean;
	hideChat?: boolean;
	hideTask?: boolean;
	hasItemPath?: boolean;
}

@inject("session", "showAlert", "showToast")
@observer
export class ItemRightPart extends React.Component<ItemRightPartProps> {

	@observable activeTabId = TAB.ACTIVITY;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: ItemRightPartProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const { store, areas, /*hideNotes, hideDocuments, hideChat,*/ hasItemPath } = this.props;
		const hideNotes = true;
		const hideDocuments = true;
		const hideChat = true;
		const item = store.item!;
		const classes = classNames(
			"slds-size_1-of-1 slds-large-size_1-of-3",
			hasItemPath ? "fa-item-part" : "fa-height-100"
		);
		return (
			<Col className={classes}>
				<Card heading="" hasNoHeader className="fa-height-100" bodyClassName="slds-m-around_none">
					<Tabs selectedIndex={this.activeTabId} onSelect={this.onRightSelect} className="full-height">
						{
							!hideNotes &&
							<TabsPanel label={"Notizen"}>
								{
									this.activeTabId === TAB.NOTES &&
									<NotesTab
										relatedToId={store.id!}
										store={(store as any).notesStore}
										notes={(store as any).notesStore.notes}
									/>
								}
							</TabsPanel>
						}
						{
							!hideDocuments &&
							<TabsPanel label={"Dokumente (" + (store.counters?.documentCount || "?") + ")"}>
								{this.activeTabId === TAB.DOCUMENTS && <DocumentsTab store={store} areas={areas || []} />}
							</TabsPanel>
						}
						{
							!hideChat && (
								<TabsPanel label={"Chat"}>
									{this.activeTabId === TAB.CHAT && "TBD"}
								</TabsPanel>
							)
						}
						<TabsPanel label="Aktivität">
							{
								this.activeTabId === TAB.ACTIVITY &&
								<ActivityPortlet {...Object.assign({}, this.props, { item: item, onSave: this.onSavePortlet })} />
							}
						</TabsPanel>
						{
							item.isDoc &&
							<TabsPanel label={"Stage History (" + store.counters?.stageHistoryCount + ")"}>
								{
									this.activeTabId === TAB.STAGE_HISTORY &&
									<StageHistoryTab store={this.props.store as DocStore} />
								}
							</TabsPanel>
						}
					</Tabs>
				</Card>
			</Col>
		);
	}

	private onRightSelect = (tabId: number) => {
		this.activeTabId = tabId;
	};

	private onSavePortlet = async (type: string, data: any) => {
		let store: DocStore, payload: any, title: string;

		switch (type) {
			case ActivityFormTypes.TASK:
				title = "Task";
				store = TaskStoreModel.create({});
				payload = FormParser.parseTask(data, this.ctx.session.sessionInfo!.user);
				break;
			default:
				throw new Error("Undefined store set");
		}

		try {
			store.create(payload);
			await store.store();
			this.ctx.showToast("success", title + " stored");
		} catch (error: any) {
			this.ctx.showAlert(
				"error",
				"Could not store" + title + ": " + (error.detail ? error.detail : error.title ? error.title : error)
			);
		}
	};

}
