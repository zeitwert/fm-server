
import { Avatar, Button, GlobalHeaderProfile, Popover } from "@salesforce/design-system-react";
import { GLOBAL_HEADER_PROFILE } from "@salesforce/design-system-react/utilities/constants";
import { session } from "@zeitwert/ui-model";
import React from "react";

const HeaderProfileCustomContent = (props: any) => (
	<div id="header-profile-custom-popover-content">
		<div className="slds-m-top_x-small">
			<div className="slds-tile slds-tile_board">
				<div className="slds-tile__detail">
					<hr style={{ marginBlockStart: "6px", marginBlockEnd: "6px" }} />
					<dl className="slds-list_horizontal slds-wrap">
						<dt className="slds-item_label slds-text-color_weak slds-truncate">Email:</dt>
						<dd className="slds-item_detail slds-truncate">{props.email}</dd>
						<dt className="slds-item_label slds-text-color_weak slds-truncate">Mandant:</dt>
						<dd className="slds-item_detail slds-truncate">{props.tenant}</dd>
						<dt className="slds-item_label slds-text-color_weak slds-truncate">Kunde:</dt>
						<dd className="slds-item_detail slds-truncate">{props.account?.name ?? "Kein Kunde"}</dd>
					</dl>
					<hr style={{ marginBlockStart: "6px", marginBlockEnd: "6px" }} />
					<dl className="slds-list_horizontal slds-wrap">
						<dt className="slds-item_label slds-text-color_weak slds-truncate">Application:</dt>
						<dd className="slds-item_detail slds-truncate">{session.sessionInfo?.applicationName ?? "???"}</dd>
						<dt className="slds-item_label slds-text-color_weak slds-truncate">Version:</dt>
						<dd className="slds-item_detail slds-truncate">{session.sessionInfo?.applicationVersion ?? "???"}</dd>
					</dl>
					<hr style={{ marginBlockStart: "6px", marginBlockEnd: "6px" }} />
					<p className="slds-truncate">
						{/*
						<Button variant="base" className="slds-m-right_medium" onClick={props.onSettings}>
							Settings
						</Button>
						*/}
						<Button iconCategory="utility" iconName="logout" iconPosition="left" onClick={props.onLogout}>
							Log Out
						</Button>
					</p>
				</div>
				{/*
				<p className="tile__title slds-text-heading_small">
					{props.emailProvider}
					{props.hasExternalAuthentication && (
						<Button
							variant="base"
							className="slds-text-body_regular slds-m-left_small"
							onClick={props.onDisconnect}
						>
							Disconnect
						</Button>
					)}
					{!props.hasExternalAuthentication && (
						<Button
							variant="base"
							className="slds-text-body_regular slds-m-left_small"
							onClick={props.onConnect}
						>
							Connect
						</Button>
					)}
				</p>
				*/}
			</div>
		</div>
	</div>
);

interface ProfileProps {
	onLogout?: () => void;
}

export default class Profile extends React.Component<ProfileProps> {

	static displayName = GLOBAL_HEADER_PROFILE;

	render() {
		const { onLogout } = this.props;
		const sessionInfo = session?.sessionInfo;
		const user = sessionInfo?.user;
		return (
			<GlobalHeaderProfile
				userName={user?.caption}
				avatar={
					<Avatar
						variant="user"
						size="medium"
						imgSrc={session.avatarUrl(user!.id)}
						imgAlt={user!.caption}
						label={user!.caption}
					/>
				}
				popover={
					session && (
						<Popover
							id="header-profile-popover-id"
							heading={user?.caption}
							align="top right"
							body={
								<HeaderProfileCustomContent
									name={user?.caption}
									email={user?.email}
									tenant={sessionInfo?.tenant?.caption}
									account={sessionInfo?.account}
									onLogout={() => {
										session?.logout();
										onLogout && onLogout();
									}}
									hasExternalAuthentication={false}
									onConnect={() => { }}
									onDisconnect={() => { }}
								/>
							}
						>
							{" "}
						</Popover>
					)
				}
			/>
		);
	}

}
