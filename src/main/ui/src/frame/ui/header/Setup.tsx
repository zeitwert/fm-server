import { Dropdown, GlobalHeaderSetup } from "@salesforce/design-system-react";
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
