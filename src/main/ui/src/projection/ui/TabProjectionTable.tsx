
import { DataTable, DataTableCell, DataTableColumn } from "@salesforce/design-system-react";
import { DATA_TABLE_CELL } from "@salesforce/design-system-react/utilities/constants";
import { observer } from "mobx-react";
import React from "react";
import { ProjectionResult } from "../../building/ui/ProjectionResult";

const CCY_FMT = new Intl.NumberFormat('de-CH', { /*style: 'currency', currency: 'CHF',*/ maximumFractionDigits: 0 });
const NR_FMT = new Intl.NumberFormat('de-CH', { minimumFractionDigits: 6 });
const PC_FMT = new Intl.NumberFormat('de-CH', { minimumFractionDigits: 0, maximumFractionDigits: 0 });

const NumericCell: React.FunctionComponent<any> = ({ children, displayName, ...props }: any) => {
	return (
		<DataTableCell {...props} >
			<div className="slds-float_right">{children}</div>
		</DataTableCell>
	);
};
NumericCell.displayName = DATA_TABLE_CELL;

const TextCell: React.FunctionComponent<any> = ({ children, displayName, ...props }: any) => {
	return (
		<DataTableCell {...props} >
			<div className="slds-truncate">
				{children}
			</div>
		</DataTableCell>
	);
};
TextCell.displayName = DATA_TABLE_CELL;

const columns = [
	<DataTableColumn key="year" label="Jahr" property="year" width="5%" />,
	<DataTableColumn key="originalValue" label="Neuwert" property="originalValue" width="8%">
		<NumericCell />
	</DataTableColumn>,
	<DataTableColumn key="timeRate" label="ZN Wert" property="timeRate" width="5%">
		<NumericCell />
	</DataTableColumn>,
	<DataTableColumn key="timeValue" label="Zeitwert" property="timeValue" width="8%">
		<NumericCell />
	</DataTableColumn>,
	<DataTableColumn key="maintenanceCosts" label="IH Kosten" property="maintenanceCosts" width="8%">
		<NumericCell />
	</DataTableColumn>,
	<DataTableColumn key="restorationCosts" label="IS Kosten" property="restorationCosts" width="8%">
		<NumericCell />
	</DataTableColumn>,
	<DataTableColumn key="restorationPart" label="IS Element" property="restorationPart" width="28%" truncate={true}>
		<TextCell />
	</DataTableColumn>,
	<DataTableColumn key="restorationBuilding" label="IS Gebäude" property="restorationBuilding" width="30%" truncate={true}>
		<TextCell />
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
				timeRate: PC_FMT.format(100.0 * period.timeValue / period.originalValue),
				restorationCosts: period.restorationCosts ? CCY_FMT.format(period.restorationCosts) : "",
				restorationPart: "",
				restorationBuilding: "",
				techPart: period.techPart ? PC_FMT.format(100.0 * period.techPart) : "",
				techRate: period.techRate ? NR_FMT.format(period.techRate) : "",
				maintenanceRate: period.maintenanceRate ? PC_FMT.format(100.0 * period.maintenanceRate) : "",
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
		// Align column headers right
		setTimeout(() => {
			document.querySelectorAll("th").forEach(th => {
				if (["Jahr", "IS Element", "IS Gebäude"].indexOf(th.getAttribute("aria-label")!) < 0) {
					const thDiv = th.querySelectorAll("div")[2];
					thDiv.style.flexDirection = "row-reverse";
					thDiv.style.paddingRight = "0.75em";
				}
			});
			document.querySelectorAll("td").forEach(td => {
				if (td.classList.contains("slds-truncate")) {
					td.style.maxWidth = "1px";
				}
			});
		}, 10);
		return (
			<div style={{ position: "relative", overflow: "auto", height: "100%" }}>
				<div style={{ position: "absolute", top: "0", left: "0", bottom: "0", right: "0" }}>
					<DataTable items={timeValues} striped fixedHeader>
						{columns}
					</DataTable>
				</div>
			</div>
		);
	}

}
