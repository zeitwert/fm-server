
import { Avatar, Card, ExpandableSection, Icon } from "@salesforce/design-system-react";
import { API, Config, DateFormat, Enumerated, session } from "@zeitwert/ui-model";
import { computed, makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";
import ReactMarkdown from "react-markdown";

interface Activity {
	item: Enumerated; // rating: building, task: task
	relatedTo: Enumerated; // rating: building, task: obj
	owner: Enumerated;
	user: Enumerated;
	dueAt: Date;
	subject: string;
	content: string;
	priority: Enumerated;
}

@observer
export default class HomeCardOpenActivities extends React.Component {

	@observable activities: Activity[] = [];

	@computed get futureActivities(): Activity[] {
		const now = new Date();
		return this.activities.filter(t => t.dueAt! > now);
	}

	@computed get overdueActivities(): Activity[] {
		const now = new Date();
		return this.activities.filter(t => t.dueAt! <= now);
	}

	constructor(props: any) {
		super(props);
		makeObservable(this);
	}

	componentDidMount() {
		this.loadActivities();
	}

	render() {
		return (
			<Card
				icon={<Icon category="custom" name="custom24" size="small" />}
				heading={<b>{`Laufende Aktivitäten (${this.activities.length})`}</b>}
				className="fa-height-100"
				bodyClassName="slds-m-around_none slds-p-horizontal_xx-small"
			>
				{
					!this.activities.length &&
					<p className="slds-m-horizontal_medium">Keine laufenden Aktivitäten.</p>
				}
				{
					!!this.activities.length &&
					<div className="slds-is-relative">
						<div className="slds-feed">
							<ul className="slds-feed__list">
								{
									!!this.futureActivities.length &&
									this.futureActivities.map((task, index) => (
										<li className="slds-feed__item slds-p-bottom_none" key={"task-" + index}>
											<ActivityView task={task} />
											<hr style={{ marginBlockStart: "0px", marginBlockEnd: 0 }} />
										</li>
									))
								}
								{
									!!this.overdueActivities.length &&
									<ExpandableSection title="Überfällig">
										{
											this.overdueActivities.map((task, index) => (
												<li className="slds-feed__item" key={"task-" + index}>
													<ActivityView task={task} />
													<hr style={{ marginBlockStart: "0px", marginBlockEnd: 0 }} />
												</li>
											))
										}
									</ExpandableSection>
								}
							</ul>
						</div>
					</div>
				}
				{
					/*
					!!this.activities.length &&
					<div>
						{
							this.activities.map((a: Activity, index: number) => (
								<article className="slds-tile slds-media" key={"todo-" + index}>
									{
										a.item.itemType?.id === "doc_task" &&
										<div className="slds-media__body">
											<h3 className="slds-tile__title slds-truncate">
												<a href={"/task/" + a.item.id}><strong>{a.subject}</strong></a>
												<br />
												{a.content}
											</h3>
											<div className="slds-tile__detail">
												<p className="slds-truncate">Termin: {DateFormat.compact(a.dueAt)} für {a.user.name}</p>
											</div>
										</div>
									}
									{
										a.item.itemType?.id === "obj_building" &&
										<div className="slds-media__body">
											<h3 className="slds-tile__title slds-truncate">
												<strong>Bewertung</strong> <a href={"/building/" + a.item.id}><strong>{a.subject}</strong></a>
												<br />
												{a.content}
											</h3>
											<div className="slds-tile__detail">
												<p className="slds-truncate">Begehung am {DateFormat.compact(a.dueAt)}{a.user ? ` (${a.user.name})` : ""}</p>
											</div>
										</div>
									}
								</article>
							))
						}
					</div>
					*/
				}
			</Card>
		);
	}

	private async loadActivities() {
		const rsp = await API.get(Config.getRestUrl("home", "openActivities/" + session.sessionInfo?.account?.id))
		this.activities = (rsp.data as Activity[]).sort((a: Activity, b: Activity) => (a.dueAt! > b.dueAt! ? -1 : 1));
		this.activities.forEach((a: Activity) => { a.dueAt = new Date(Date.parse(a.dueAt as any)); });
	}

}

interface ActivityViewProps {
	task: Activity;
}

@observer
class ActivityView extends React.Component<ActivityViewProps> {

	render() {

		const task = this.props.task;
		const user = task.user!;
		const userName = user.name;
		const userAvatar = session.avatarUrl(user.id);
		const dueAt = DateFormat.compact(task.dueAt, false);
		const dueAtRelative = DateFormat.relativeTime(task.dueAt!);

		return (
			<article className="slds-post">
				<header className="slds-post__header slds-media slds-m-bottom_xx-small">
					<div className="slds-media__figure">
						<Avatar
							variant="user"
							size="medium"
							imgSrc={userAvatar}
							label={userName}
						/>
					</div>
					<div className="slds-media__body">
						<div className="slds-clearfix">
							<div className="slds-float_left">
								<p>
									<strong><a href={`/${task.relatedTo.itemType?.id.substring(4)}/${task.relatedTo.id}`}>{task.relatedTo.name}</a></strong>
								</p>
							</div>
							<div className="slds-float_right">
								{dueAtRelative}
							</div>
						</div>
						<p className="slds-text-body_small">
							{dueAt} ⋅ <a href={`/user/${user.id}`} title={userName}>{this.getUserName(user)}</a>
						</p>
					</div>
				</header>
				<div className="slds-post__content xslds-text-longform slds-m-bottom_x-small">
					<div><strong><a href={`/${task.item.itemType?.id.substring(4)}/${task.item.id}`}>{task.subject || "(ohne Titel)"}</a></strong></div>
					{
						!!task.content &&
						<ReactMarkdown className="fa-task-content">
							{task.content}
						</ReactMarkdown>
					}
				</div>
			</article>
		);
	}

	private getUserName(user: Enumerated) {
		return user.id == session.sessionInfo?.user.id ? "Du" : user.name;
	}

}
