
import DataTable from "@salesforce/design-system-react/components/data-table";
import DataTableCell from "@salesforce/design-system-react/components/data-table/cell";
import DataTableColumn from "@salesforce/design-system-react/components/data-table/column";
import { DATA_TABLE_CELL } from "@salesforce/design-system-react/utilities/constants";
import { observer } from "mobx-react";
import React from "react";
import { ProjectionResult } from "../../building/ui/ProjectionResult";

const CCY_FMT = new Intl.NumberFormat('de-CH', { /*style: 'currency', currency: 'CHF',*/ maximumFractionDigits: 0 });
const NR_FMT = new Intl.NumberFormat('de-CH', { minimumFractionDigits: 6 });

const NumericCell: React.FunctionComponent<any> = ({ children, displayName, ...props }: any) => {
	return (
		<DataTableCell {...props} >
			<div className="slds-float_right">{children}</div>
		</DataTableCell>
	);
};
NumericCell.displayName = DATA_TABLE_CELL;

const columns = [
	<DataTableColumn key="year" label="Jahr" property="year" width="4%" />,
	<DataTableColumn key="originalValue" label="Neuwert (indexiert)" property="originalValue" width="7%">
		<NumericCell />
	</DataTableColumn>,
	<DataTableColumn key="timeValue" label="Zustandswert" property="timeValue" width="7%">
		<NumericCell />
	</DataTableColumn>,
	<DataTableColumn key="maintenanceCosts" label="IH Kosten" property="maintenanceCosts" width="7%">
		<NumericCell />
	</DataTableColumn>,
	<DataTableColumn key="restorationCosts" label="IS Kosten" property="restorationCosts" width="7%">
		<NumericCell />
	</DataTableColumn>,
	<DataTableColumn key="restorationPart" label="IS Element" property="restorationPart" width="13%" truncate={true} />,
	<DataTableColumn key="restorationBuilding" label="IS Gebäude" property="restorationBuilding" width="40%" truncate={true} />,
	<DataTableColumn key="techPart" label="Technikanteil" property="techPart" width="5%">
		<NumericCell />
	</DataTableColumn>,
	<DataTableColumn key="techRate" label="Technikrate" property="techRate" width="5%">
		<NumericCell />
	</DataTableColumn>,
	<DataTableColumn key="maintenanceRate" label="IH Rate" property="maintenanceRate" width="5%">
		<NumericCell />
	</DataTableColumn>,
];
/*
slds-float_right
	<DataTableColumn key="restorationBacklog" label="IS Rückstände" property="restorationBacklog" />,
	<DataTableColumn key="cumulativeMaintenanceCosts" label="Kum IH Kosten" property="cumulativeMaintenanceCosts" />,
	<DataTableColumn key="cumulativeRestorationCosts" label="Kum IS Kosten" property="cumulativeRestorationCosts" />,
	<DataTableColumn key="cumulativeCosts" label="Kum IH + IS Kosten" property="cumulativeCosts" />,
	<DataTableColumn key="cumulativeCostsAndBacklog" label="Kum IS + IH + Rückstände" property="cumulativeCostsAndBacklog" />,
	<DataTableColumn key="valueLoss" label="Wertverlust" property="valueLoss" />,
*/

export interface TabProjectionTableProps {
	projection: ProjectionResult;
}

@observer
export default class TabProjectionTable extends React.Component<TabProjectionTableProps> {

	render() {
		const timeValues = this.props.projection.periodList.flatMap(period => {
			const rows = [];
			rows[0] = {
				id: period.year.toString(),
				year: period.year,
				originalValue: CCY_FMT.format(period.originalValue),
				timeValue: CCY_FMT.format(period.timeValue),
				restorationCosts: period.restorationCosts ? CCY_FMT.format(period.restorationCosts) : "",
				restorationPart: "",
				restorationBuilding: "",
				techPart: period.techPart ? NR_FMT.format(period.techPart) : "",
				techRate: period.techRate ? NR_FMT.format(period.techRate) : "",
				maintenanceRate: period.maintenanceRate ? NR_FMT.format(period.maintenanceRate) : "",
				maintenanceCosts: period.maintenanceCosts ? CCY_FMT.format(period.maintenanceCosts) : "",
			};
			if (period.restorationElements.length === 1) {
				rows[0].restorationPart = period.restorationElements[0].buildingPart.name;
				rows[0].restorationBuilding = period.restorationElements[0].building.name;
			} else if (period.restorationElements.length > 1) {
				period.restorationElements.forEach((re, index) => {
					rows[rows.length + 1] = {
						id: "tv-" + period.year + "-" + index,
						restorationPart: re.buildingPart.name,
						restorationBuilding: re.building.name,
						restorationCosts: CCY_FMT.format(re.restorationCosts),
					};
				});
			}
			return rows;
		});
		setTimeout(() => {
			document.querySelectorAll("th").forEach(tce => {
				if (["Jahr", "IS Element", "IS Gebäude"].indexOf(tce.getAttribute("aria-label")!) < 0) {
					tce.getElementsByTagName("div")[0].classList.add("slds-float_right");
				}
			});
		}, 10);
		return (
			<div style={{ overflow: "auto", height: "100%" }}>
				<DataTable items={timeValues} striped>
					{columns}
				</DataTable>
			</div>
		);
	}

}
