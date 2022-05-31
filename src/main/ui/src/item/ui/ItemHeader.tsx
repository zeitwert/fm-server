import { Button, ButtonGroup, Icon, MediaObject, PageHeaderControl } from "@salesforce/design-system-react";
import { AggregateStore, ItemPartNote } from "@zeitwert/ui-model";
import ButtonStateful from "@zeitwert/ui-slds/common/ButtonStateful";
import { PageHeader } from "@zeitwert/ui-slds/content/PageHeader";
import { AppCtx } from "App";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import { Link } from "react-router-dom";

export interface HeaderDetail {
	label: string;
	content?: string;
	link?: string;
	url?: string;
	icon?: React.ReactNode;
	truncate?: boolean;
}

interface ItemHeaderProps {
	store: AggregateStore;
	details: HeaderDetail[];
	customActions?: JSX.Element;
	editItem?: () => void;
}

@inject("session", "showAlert", "showToast")
@observer
export default class ItemHeader extends React.Component<ItemHeaderProps> {

	@observable note: ItemPartNote | undefined = undefined;
	@observable isFollowing = false;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: ItemHeaderProps) {
		super(props);
		makeObservable(this);
	}

	// async componentDidMount() {
	// 	await this.ctx.followStore.load(this.ctx.session.sessionInfo!.user);
	// }

	render() {
		const item = this.props.store.item!;
		const follow = false; //this.ctx.followStore.getFollow().find((follow) => follow.item.id === item?.id);
		const actions = (
			<>
				{/* @ts-ignore */}
				<PageHeaderControl>
					{
						false &&
						<ButtonStateful
							stateOne={{
								label: "Follow"
							}}
							stateTwo={{
								label: !!follow ? (
									<div className="slds-text-heading_small" style={{ lineHeight: 1.9 }}>
										<strong>Unfollow</strong>
									</div>
								) : (
									"Follow"
								)
							}}
							stateThree={{
								icon: "check",
								label: (
									<div className="slds-text-heading_small" style={{ lineHeight: 1.9 }}>
										<strong>Following</strong>
									</div>
								)
							}}
							active={!!follow}
							onClick={async () => {
								// if (!follow) {
								// 	await this.ctx.followStore.add(this.ctx.session.sessionInfo!.user, item);
								// } else {
								// 	await this.ctx.followStore.remove(follow);
								// }
							}}
						/>
					}
				</PageHeaderControl>
				{
					this.props.editItem &&
					// @ts-ignore
					<PageHeaderControl>
						<ButtonGroup variant="list">
							<Button onClick={() => this.props.editItem && this.props.editItem()}>Edit</Button>
						</ButtonGroup>
					</PageHeaderControl>
				}
				{/* @ts-ignore */}
				<PageHeaderControl>{this.props.customActions}</PageHeaderControl>
			</>
		);
		const renderHeaderContent = (header: HeaderDetail) => {
			if (header.link) {
				return (
					<Link to={header.link}>
						<MediaObject body={header.content} figure={header.icon} verticalCenter />
					</Link>
				);
			} else if (header.url) {
				return (
					<a href={header.url}>
						<MediaObject body={header.content} figure={header.icon} verticalCenter />
					</a>
				);
			}
			return <MediaObject body={header.content} figure={header.icon} verticalCenter />;
		};
		return (
			<PageHeader
				icon={
					<Icon
						assistiveText={{ label: item.type.labelSingular }}
						category={item.type.iconCategory}
						name={item.type.iconName}
					/>
				}
				legend={item.type.labelSingular}
				title={(item as any)?.name || item.caption}
				topActions={actions}
				details={this.props.details.map((header) => ({
					label: header.label,
					content: renderHeaderContent(header),
					truncate: header.truncate
				}))}
			/>
		);
	}

}
