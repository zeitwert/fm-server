import { Session } from "@comunas/ui-model";
import Avatar from "@salesforce/design-system-react/components/avatar";
import Button from "@salesforce/design-system-react/components/button";
import GlobalHeaderProfile from "@salesforce/design-system-react/components/global-header/profile";
import Popover from "@salesforce/design-system-react/components/popover";
import { GLOBAL_HEADER_PROFILE } from "@salesforce/design-system-react/utilities/constants";
import React from "react";

const HeaderProfileCustomContent = (props: any) => (
	<div id="header-profile-custom-popover-content">
		<div className="slds-m-top_x-small">
			<div className="slds-tile slds-tile_board">
				<div className="slds-tile__detail">
					<dl className="slds-list_horizontal slds-wrap">
						<dt className="slds-item_label slds-text-color_weak slds-truncate">Email:</dt>
						<dd className="slds-item_detail slds-truncate">{props.email}</dd>
						<dt className="slds-item_label slds-text-color_weak slds-truncate">Mandant:</dt>
						<dd className="slds-item_detail slds-truncate">{props.tenant}</dd>
						<dt className="slds-item_label slds-text-color_weak slds-truncate">Community:</dt>
						<dd className="slds-item_detail slds-truncate">{props.customValues.community?.name || "No community"}</dd>
					</dl>
					<p className="slds-truncate">
						<Button variant="base" className="slds-m-right_medium" onClick={props.onSettings}>
							Settings
						</Button>
						<Button variant="base" onClick={props.onLogout}>
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
	session?: Session;
	onLogout?: () => void;
}

export default class Profile extends React.Component<ProfileProps> {

	static displayName = GLOBAL_HEADER_PROFILE;

	render() {
		const { session, onLogout } = this.props;
		const sessionInfo = session?.sessionInfo;
		const user = sessionInfo?.user;
		return (
			<GlobalHeaderProfile
				userName={user?.caption}
				avatar={
					<Avatar
						variant="user"
						size="medium"
						imgSrc={user!.picture}
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
									emailProvider={user?.emailProvider?.name}
									email={user?.email}
									tenant={sessionInfo?.tenant?.caption}
									onLogout={() => {
										session?.logout();
										onLogout && onLogout();
									}}
									hasExternalAuthentication={false}
									onConnect={() => { }}
									onDisconnect={() => { }}
									customValues={sessionInfo?.customValues}
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
