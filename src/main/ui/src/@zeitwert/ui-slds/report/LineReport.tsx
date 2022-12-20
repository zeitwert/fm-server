import { DataTable, DataTableColumn, DataTableRowActions, Dropdown } from "@salesforce/design-system-react";
import { makeObservable, observable, toJS } from "mobx";
import { observer } from "mobx-react";
import React from "react";
import { DataTableCellWithText } from "../custom/CustomDataTableCells";

interface LineReportProps {
	items?: any;
	options?: any[];
	dataTableCellTemplates?: any;
	isJoined?: boolean;
	fixedLayout?: boolean;
	fixedHeader?: boolean;
	sortProperty?: string;
	sortDirection?: "asc" | "desc" | undefined;
	maxColumns?: number;
	onMouseEnter?: (itemId: string) => void;
	onMouseLeave?: (itemId: string) => void;
	onClick?: (itemId: string) => void;
	onSort?: (property: string, direction: "asc" | "desc" | undefined) => void;
	onSelectionChange?: (selectedItems: any[]) => void;
}

interface SortColumn {
	property: string;
	sortDirection: "asc" | "desc" | undefined;
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

@observer
export class LineReport extends React.Component<LineReportProps> {

	@observable items: any[] = [];
	@observable selection: string[] = [];

	constructor(props: LineReportProps) {
		super(props);
		makeObservable(this);
		this.prepareItems();
	}

	componentDidUpdate(prevProps: Readonly<LineReportProps>, prevState: Readonly<{}>, snapshot?: any): void {
		if (this.props.sortProperty === prevProps.sortProperty && this.props.sortDirection === prevProps.sortDirection) {
			return;
		}
		this.prepareItems();
	}

	render() {
		const { items, options, dataTableCellTemplates, isJoined, fixedLayout, fixedHeader, maxColumns, onMouseEnter, onMouseLeave, onClick, onSelectionChange } = this.props;
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
				items={this.items}
				joined={isJoined === false ? false : true}
				fixedLayout={fixedLayout === false ? false : true}
				fixedHeader={fixedHeader === false ? false : true}
				onRowChange={this.handleChanged}
				onSort={this.handleSort}
				selection={toJS(this.selection)}
				selectRows={options || onSelectionChange ? "checkbox" : undefined}
			>
				{
					header.map((header: HeaderInfo, index: number) => {
						const columnProps = {
							key: header.value,
							label: header.label,
							property: header.value,
							width: header.width ? header.width : undefined,
							sortable: header.sortable !== undefined ? header.sortable : true,
							isSorted: header.value === this.props.sortProperty,
							sortDirection: this.props.sortDirection
						};
						const T: any = dataTableCellTemplates?.[header.template || ""] || DataTableCellWithText;
						return (
							<DataTableColumn {...columnProps}>
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

	private prepareItems() {
		let items = (this.props.items?.data as any[]).map((item, index) => {
			return Object.assign(item, {
				id: (item.id || index).toString()
			});
		});
		if (this.props.sortProperty) {
			items = items.sort((a, b) => {
				if (a[this.props.sortProperty!] > b[this.props.sortProperty!]) {
					return this.props.sortDirection === "desc" ? -1 : 1;
				} else if (a[this.props.sortProperty!] < b[this.props.sortProperty!]) {
					return this.props.sortDirection === "desc" ? 1 : -1;
				}
				return 0;
			});
		}
		this.items = items;
	}

	private handleChanged = (event: any, data: any) => {
		this.selection = data.selection;
		this.props.onSelectionChange?.(this.selection);
	};

	private handleSort = (sortColumn: SortColumn) => {
		this.props.onSort?.(sortColumn.property, sortColumn.sortDirection);
	};

}
