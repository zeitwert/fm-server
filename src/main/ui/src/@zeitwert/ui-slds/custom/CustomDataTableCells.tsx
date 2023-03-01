
import { DataTableCell, Icon } from "@salesforce/design-system-react";
import { DATA_TABLE_CELL } from "@salesforce/design-system-react/utilities/constants";
import { DateFormat, EntityTypes, NumberFormat } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import React from "react";
import { Link } from "react-router-dom";

const TEMPERATURE_TRESHOLD = 25;

export const PREFIX = "line-report-SLDSDataTableRow-";
export const POSTFIX = "-SelectRow";

export const PREFIX_LEN = PREFIX.length;
export const POSTFIX_LEN = POSTFIX.length;

class DataTableCellWithText extends React.Component<any> {

	static displayName = DATA_TABLE_CELL;

	render() {
		const { children, item, link, displayName, onHover, onClick, ...dtcProps } = this.props;
		return (
			<DataTableCell title={children ? children as string : undefined} {...dtcProps}>
				<div
					onMouseEnter={this.handleMouseEnter}
					onMouseLeave={this.handleMouseLeave}
					onClick={this.handleClick}
				>
					{children}
				</div>
			</DataTableCell>
		);
	}

	private handleMouseEnter = (e: React.MouseEvent<HTMLDivElement>) => {
		this.props.onMouseEnter?.(this.props.item.id);
	}

	private handleMouseLeave = (e: React.MouseEvent<HTMLDivElement>) => {
		this.props.onMouseLeave?.(this.props.item.id);
	}

	private handleClick = (e: React.MouseEvent<HTMLDivElement>) => {
		this.props.onClick?.(this.props.item.id);
	}

}

class DataTableCellWithLink extends React.Component<any> {

	static displayName = DATA_TABLE_CELL;

	render() {
		const { children, item, link, displayName, onHover, onClick, ...dtcProps } = this.props;
		const datum = item[link];
		return (
			<DataTableCell title={children ? children as string : undefined} {...dtcProps}>
				<div
					onMouseEnter={this.handleMouseEnter}
					onMouseLeave={this.handleMouseLeave}
					onClick={this.handleClick}
				>
					<Link to={datum}>
						{children}
					</Link>
				</div>
			</DataTableCell>
		);
	}

	private handleMouseEnter = (e: React.MouseEvent<HTMLDivElement>) => {
		this.props.onMouseEnter?.(this.props.item.id);
	}

	private handleMouseLeave = (e: React.MouseEvent<HTMLDivElement>) => {
		this.props.onMouseLeave?.(this.props.item.id);
	}

	private handleClick = (e: React.MouseEvent<HTMLDivElement>) => {
		this.props.onClick?.(this.props.item.id);
	}

}

const DataTableCellWithEntityIcon: React.FunctionComponent<any> = ({ children, item, type, link, ...props }: any) => {
	const datum = item[type];
	const linkDatum = item[link];
	const entityType = EntityTypes[datum.substring(4)];
	return (
		<DataTableCell {...props}>
			{
				entityType && (
					<Icon
						containerClassName="slds-m-right_small"
						category={entityType.iconCategory}
						name={entityType.iconName}
						size="small"
					/>
				)
			}
			{link ? <Link to={linkDatum}>{children}</Link> : children}
		</DataTableCell>
	);
};

DataTableCellWithEntityIcon.displayName = DATA_TABLE_CELL;

const DataTableCellForTemperature = ({ children, item, ...props }: any) => {
	const temperature = Math.min(Math.max(item.temperature, 0), TEMPERATURE_TRESHOLD * 2);
	return (
		<DataTableCell {...props} className={"temperature-" + temperature}>
			{children}
		</DataTableCell>
	);
};

DataTableCellForTemperature.displayName = DATA_TABLE_CELL;

const DateDataTableCell: React.FunctionComponent<any> = ({ children, item, displayName, ...props }: any) => {
	return (
		<DataTableCell title={children} {...props}>
			{DateFormat.long(children)}
		</DataTableCell>
	);
};

DateDataTableCell.displayName = DATA_TABLE_CELL;

const CurrencyDataTableCell: React.FunctionComponent = ({ children, ...props }: any) => {
	return (
		<DataTableCell {...props}>
			<Grid isVertical={false}>
				<Col>{children.currency}</Col>
				<Col className="slds-text-align_right">{NumberFormat.formatNumber(children.value)}</Col>
			</Grid>
		</DataTableCell>
	);
};

CurrencyDataTableCell.displayName = DATA_TABLE_CELL;

const AccountDataTableCell: React.FunctionComponent = ({ children, ...props }: any) => {
	return (
		<DataTableCell {...props}>
			<Link to={"account/" + children.id}>{children.caption}</Link>
		</DataTableCell>
	);
};

AccountDataTableCell.displayName = DATA_TABLE_CELL;

const ItemDataTableCell: React.FunctionComponent<any> = ({ children, ...props }: any) => {
	const entityType = EntityTypes[children.type];
	return (
		<DataTableCell {...props}>
			<div className="slds-truncate">
				<Icon
					containerClassName="slds-m-right_small"
					category={entityType.iconCategory}
					name={entityType.iconName}
					size="small"
				/>
				<Link to={entityType.type + "/" + children.id}>{children.caption}</Link>
			</div>
		</DataTableCell>
	);
};

ItemDataTableCell.displayName = DATA_TABLE_CELL;

export {
	DataTableCellWithText,
	DataTableCellWithLink,
	DataTableCellWithEntityIcon,
	DataTableCellForTemperature,
	DateDataTableCell,
	CurrencyDataTableCell,
	AccountDataTableCell,
	ItemDataTableCell
};

