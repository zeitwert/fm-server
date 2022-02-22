
import Tabs from "@salesforce/design-system-react/components/tabs";
import TabsPanel from "@salesforce/design-system-react/components/tabs/panel";
import { Account, ActivityStore, ActivityStoreModel, Aggregate } from "@zeitwert/ui-model";
import { TaskForm } from "activity/forms/TaskForm";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";
import ActivityPortletTimeline from "./timeline/ActivityPortletTimeline";

enum TAB {
	TIMELINE = 0,
	NEW_TASK = 1
}

export enum ActivityFormTypes {
	TASK = "task"
}

export interface ActivityProps {
	item: Aggregate;
	account?: Account;
	hideTask?: boolean;
	onSave: (type: string, data: any) => Promise<any>;
}

@observer
export class ActivityPortlet extends React.Component<ActivityProps> {
	@observable store: ActivityStore = ActivityStoreModel.create();
	@observable activeTabId = TAB.TIMELINE;

	constructor(props: ActivityProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		const { item } = this.props;
		await this.store.loadActivitiesByItem(item);
	}

	get hideTask() {
		return this.props.hideTask === true;
	}

	render() {
		return (
			<>
				<Tabs className="full-height">
					{this.renderTimelineTab()}
					{this.renderFormTabs()}
				</Tabs>
			</>
		);
	}

	private renderTimelineTab() {
		return (
			<TabsPanel key={TAB.TIMELINE} label="Timeline">
				<div className="slds-m-around_medium">
					<ActivityPortletTimeline activities={this.store.activities} />
				</div>
			</TabsPanel>
		);
	}

	private renderFormTabs() {
		const { item, account } = this.props;
		const tabs = [];
		if (!this.hideTask) {
			tabs.push(
				<TabsPanel key={TAB.NEW_TASK} label="New Task">
					<div className="slds-m-around_medium">
						<TaskForm item={item} account={account} onSave={this.onActivitySave} />
					</div>
				</TabsPanel>
			);
		}
		return tabs;
	}

	private onActivitySave = async (type: string, data: any) => {
		const { item, onSave } = this.props;
		await onSave(type, data);
		await this.store.loadActivitiesByItem(item);
		this.activeTabId = TAB.TIMELINE;
	};
}
