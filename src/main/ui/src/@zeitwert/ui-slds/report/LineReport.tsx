import { DataTable, DataTableColumn, DataTableRowActions, Dropdown } from "@salesforce/design-system-react";
import { toJS } from "mobx";
import React from "react";
import { DataTableCellWithText } from "../custom/CustomDataTableCells";

interface LineReportProps {
	items?: any;
	options?: any[];
	dataTableCellTemplates?: any;
	isJoined?: boolean;
	fixedLayout?: boolean;
	fixedHeader?: boolean;
	maxColumns?: number;
	onMouseEnter?: (itemId: string) => void;
	onMouseLeave?: (itemId: string) => void;
	onClick?: (itemId: string) => void;
}

interface SortColumn {
	property: string;
	sortDirection: "asc" | "desc" | undefined;
}

interface LineReportState {
	items: any[];
	selection: any[];
	isStateChange: boolean;
}

interface HeaderInfo {
	id: string;
	label: string;
	value: string;
	template?: string;
	link?: string;
	type?: string;
	width?: string;
	sortable?: boolean;
}

export class LineReport extends React.Component<LineReportProps, LineReportState> {

	static getDerivedStateFromProps(props: any, state: any) {
		// guard against call after setState (from handleSort, f.ex.)
		if (state.isStateChange) {
			return { isStateChange: false };
		}
		const itemsWithId = (props.items?.data as any[]).map((item, index) => {
			return Object.assign(item, {
				id: (item.id ? item.id : index).toString()
			});
		});
		return {
			items: itemsWithId
		};
	}

	state: LineReportState = {
		items: [],
		selection: [],
		isStateChange: false
	};

	private handleChanged = (event: any, data: any) => {
		// if (this.props.onClick) {
		// 	this.props.onClick(data.selection?.[0]);
		// }
		this.setState({ selection: data.selection });
	};

	private handleSort = (sortColumn: SortColumn) => {
		const sortProperty = sortColumn.property;
		const sortDirection = sortColumn.sortDirection;
		const newState = {
			items: [...this.state.items],
			isStateChange: true
		};
		newState.items = newState.items.sort((a, b) => {
			let val = 0;
			if (a[sortProperty] > b[sortProperty]) {
				val = 1;
			} else if (a[sortProperty] < b[sortProperty]) {
				val = -1;
			}
			if (sortDirection === "desc") {
				val *= -1;
			}
			return val;
		});
		this.setState(newState);
	};

	render() {
		const { items, options, dataTableCellTemplates, isJoined, fixedLayout, fixedHeader, maxColumns, onMouseEnter, onMouseLeave, onClick } = this.props;
		if (!items) {
			return <div />;
		}
		let header = toJS(items.header);
		if (maxColumns) {
			header = header.slice(0, maxColumns);
		}
		return (
			<DataTable
				id="line-report"
				items={this.state.items}
				joined={isJoined === false ? false : true}
				fixedLayout={fixedLayout === false ? false : true}
				fixedHeader={fixedHeader === false ? false : true}
				onRowChange={this.handleChanged}
				onSort={this.handleSort}
				selection={this.state.selection}
				selectRows={options ? "checkbox" : undefined}
			>
				{
					header.map((header: HeaderInfo, index: number) => {
						const props = {
							key: header.value,
							label: header.label,
							property: header.value,
							width: header.width ? header.width : undefined,
							sortable: header.sortable !== undefined ? header.sortable : true
						};
						const T: any = dataTableCellTemplates?.[header.template || ""] || DataTableCellWithText;
						return (
							<DataTableColumn {...props}>
								<T
									type={header.type}
									link={header.link}
									onMouseEnter={onMouseEnter}
									onMouseLeave={onMouseLeave}
									onClick={onClick}
								/>
							</DataTableColumn>
						);
					})
				}
				{
					options && options.length && (
						<DataTableRowActions options={options} dropdown={<Dropdown length="5" />} />
					)
				}
			</DataTable>
		);
	}

}
