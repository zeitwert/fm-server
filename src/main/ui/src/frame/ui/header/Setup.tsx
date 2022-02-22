import GlobalHeaderSetup from "@salesforce/design-system-react/components/global-header/setup";
import Dropdown from "@salesforce/design-system-react/components/menu-dropdown";
import { GLOBAL_HEADER_SETUP } from "@salesforce/design-system-react/utilities/constants";
import React from "react";

export default class Setup extends React.Component {
	static displayName = GLOBAL_HEADER_SETUP;

	render() {
		return (
			<GlobalHeaderSetup
				dropdown={
					<Dropdown
						id="header-setup-dropdown-id"
						options={[
							{
								id: "setupOptionOne",
								label: "Setup Option One"
							},
							{
								id: "setupOptionTwo",
								label: "Setup Option Two"
							}
						]}
					/>
				}
			/>
		);
	}
}
