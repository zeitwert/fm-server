import DataTableRowActions from "@salesforce/design-system-react/components/data-table/row-actions";
import { DATA_TABLE_ROW_ACTIONS } from "@salesforce/design-system-react/utilities/constants";
import React from "react";

export class DynamicDataTableRowActions extends React.Component<any> {
	static displayName = DATA_TABLE_ROW_ACTIONS;

	render() {
		if (!this.props.item.showActions) {
			return <td style={{ height: "31px" }}></td>;
		}
		// @ts-ignore
		return <DataTableRowActions {...this.props}></DataTableRowActions>;
	}
}
