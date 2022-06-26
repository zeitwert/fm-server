import { DataTable, DataTableColumn, DataTableRowActions, Dropdown } from "@salesforce/design-system-react";
import { toJS } from "mobx";
import React from "react";
import { DataTableCellWithText } from "../custom/CustomDataTableCells";

interface ReportLineProps {
	items?: any;
	options?: any[];
	templates?: any;
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

interface ReportLineState {
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

export class ReportLine extends React.Component<ReportLineProps, ReportLineState> {

	static getDerivedStateFromProps(props: any, state: any) {
		// guard against call after setState (from handleSort, f.ex.)
		if (state.isStateChange) {
			return { isStateChange: false };
		}
		return {
			items: props.items ? props.items.data : undefined
		};
	}

	state: ReportLineState = {
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
		const { items, options, templates, isJoined, fixedLayout, fixedHeader, maxColumns, onMouseEnter, onMouseLeave, onClick } = this.props;
		if (!items) {
			return <div />;
		}
		let header = toJS(items.header);
		if (maxColumns) {
			header = header.slice(0, maxColumns);
		}
		const itemsWithId = this.state.items.map((item, index) => {
			return Object.assign(item, {
				id: (item.id ? item.id : index).toString()
			});
		});
		return (
			<DataTable
				id="line-report"
				items={itemsWithId}
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
						if (templates) {
							const T: any = templates[header.template || ""];
							if (T) {
								return (
									<DataTableColumn {...props}>
										<T type={header.type} link={header.link} onMouseEnter={onMouseEnter} onMouseLeave={onMouseLeave} onClick={onClick} />
									</DataTableColumn>
								);
							}
						}
						return (
							<DataTableColumn {...props}>
								<DataTableCellWithText type={header.type} link={header.link} onMouseEnter={onMouseEnter} onMouseLeave={onMouseLeave} onClick={onClick} />
							</DataTableColumn>
						);
						// return <DataTableColumn {...props} />;
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
