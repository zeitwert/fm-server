
import { Avatar, Button, GlobalHeaderNotifications, MediaObject, Popover } from "@salesforce/design-system-react";
import { GLOBAL_HEADER_NOTIFICATIONS } from "@salesforce/design-system-react/utilities/constants";
import { DateFormat, session } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";
import { Link } from "react-router-dom";

// const MAX_NOTIFICATIONS = 5;

interface NotificationsProps {
	// follow: Follow[];
}

@observer
export default class Notifications extends React.Component<NotificationsProps> {
	static displayName = GLOBAL_HEADER_NOTIFICATIONS;

	@observable notifications: any[] = [];

	constructor(props: NotificationsProps) {
		super(props);
		makeObservable(this);
	}

	// componentDidUpdate(prevProps: Readonly<NotificationsProps>) {
	// 	if (prevProps.follow.length !== this.props.follow.length) {
	// 		this.notifications = this.props.follow
	// 			.map((f) => ({
	// 				id: f.item.id,
	// 				item: f.item,
	// 				itemTypeId: f.item.type.type,
	// 				user: f.item.meta?.modifiedByUser,
	// 				date: f.item.meta?.modifiedAt,
	// 				isRead: false
	// 			}))
	// 			.sort((n1, n2) => (n1.item.meta!.modifiedAt < n2.item.meta!.modifiedAt ? 1 : -1))
	// 			.slice(0, MAX_NOTIFICATIONS);
	// 	}
	// }

	render() {
		return (
			<GlobalHeaderNotifications
				notificationCount={this.notifications.length}
				popover={
					<Popover
						id="header-notifications-popover-id"
						align="top right"
						heading={
							<Grid isVertical={false} className="slds-grid_vertical-align-center">
								<Col className="slds-p-right_small">Notifications</Col>
								<Col>
									{this.notifications.length > 0 && (
										<Button
											label="Dismiss all"
											variant="brand"
											onClick={() => (this.notifications = [])}
										/>
									)}
								</Col>
							</Grid>
						}
						ariaLabelledby="header-notifications-custom-popover-content"
						body={this.renderContent(this.notifications)}
						classNameBody="slds-p-around_none"
					>
						{" "}
					</Popover>
				}
			/>
		);
	}

	renderContent(notifications: any[]) {
		if (!notifications.length) {
			return <div className="slds-p-around_medium">No new notifications.</div>;
		}
		return (
			<ul id="header-notifications-custom-popover-content">
				{notifications.map((notification: any) => (
					<li
						className="slds-global-header__notification slds-p-horizontal_small"
						key={`notification-item-${notification.id}`}
					>
						<MediaObject
							body={
								<Grid isVertical={false} className="slds-grid_vertical-align-center">
									<Col className="slds-size_10-of-12">
										<p>
											{notification.user.caption} modified "Remarks" from{" "}
											<Link to={"/" + notification.itemTypeId + "/" + notification.id}>
												{notification.item.name}
											</Link>
										</p>
										<p className="slds-m-top_xx-small slds-text-color_weak">
											{DateFormat.relativeTime(notification.date)}
										</p>
									</Col>
									<Col className="slds-size_2-of-12 slds-text-align_right">
										<Button
											className="slds-p-horizontal_x-small slds-p-vertical_xx-small"
											iconCategory="utility"
											iconName="check"
											iconSize="medium"
											variant="neutral"
											onClick={(event: any) => {
												event.preventDefault();
												const index = this.notifications.findIndex(
													(n) => n.id === notification.id
												);
												if (index > -1) {
													this.notifications.splice(index, 1);
												}
											}}
										/>
									</Col>
								</Grid>
							}
							figure={
								<Avatar
									variant="user"
									size="medium"
									imgSrc={session.avatarUrl(notification.user.id)}
									imgAlt={notification.user.caption}
									label={notification.user.caption}
								/>
							}
							verticalCenter
						/>
					</li>
				))}
			</ul>
		);
	}
}
