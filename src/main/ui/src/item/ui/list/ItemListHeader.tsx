import { Button, ButtonGroup, Dropdown, DropdownTrigger, Icon, PageHeader, PageHeaderControl } from "@salesforce/design-system-react";
import { Enumerated } from "@zeitwert/ui-model";
import { ScrollablePageHeader } from "@zeitwert/ui-slds/content/ScrollablePageHeader";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import moment from "moment";
import React from "react";

export interface ItemListHeaderProps {
	label: string;
	iconCategory: any;
	iconName: string;
	templateList: Enumerated[];
	defaultTemplateId: string;
	actionButtons: React.ReactNode[];
	hasMap?: boolean;
	showMap?: boolean;
	modifiedAt?: Date;
	reportData: any;
	onRefresh: () => void;
	onSelectTemplate: (templateId: string) => void;
	onShowMap?: (showMap: boolean) => void;
}

@observer
export default class ItemListHeader extends React.Component<ItemListHeaderProps> {

	@observable activeTemplateId?= this.props.defaultTemplateId;
	@observable modifiedAtText = "";
	interval?: NodeJS.Timer;

	constructor(props: ItemListHeaderProps) {
		super(props);
		makeObservable(this);
	}

	componentDidMount() {
		this.interval = setInterval(this.updateModifiedAtText.bind(this), 10000);
		this.updateModifiedAtText();
	}

	componentDidUpdate() {
		this.updateModifiedAtText();
	}

	componentWillUnmount() {
		clearInterval(this.interval!);
	}

	render() {
		const { label, iconCategory, iconName, templateList, actionButtons, reportData, hasMap, onRefresh, onSelectTemplate } = this.props;
		const actions = () => (
			// @ts-ignore
			<PageHeaderControl>
				<ButtonGroup variant="list">{actionButtons}</ButtonGroup>
			</PageHeaderControl>
		);
		const controls = () => (
			<>
				{/* @ts-ignore */}
				<PageHeaderControl>
					<Dropdown
						align="right"
						id="page-header-dropdown-object-home-content-right"
						options={[
							{ label: "Menu Item One", value: "A0" },
							{ label: "Menu Item Two", value: "B0" },
							{ label: "Menu Item Three", value: "C0" },
							{ type: "divider" },
							{ label: "Menu Item Four", value: "D0" }
						]}
					>
						<DropdownTrigger>
							<Button
								assistiveText={{ icon: "List View Controls" }}
								iconCategory="utility"
								iconName="settings"
								iconVariant="more"
							/>
						</DropdownTrigger>
					</Dropdown>
				</PageHeaderControl>
				{/* @ts-ignore */}
				<PageHeaderControl>
					<Dropdown
						align="right"
						assistiveText={{ icon: "Change view" }}
						iconCategory="utility"
						iconName="settings"
						iconVariant="more"
						id="page-header-dropdown-object-home-content-right-2"
						options={[
							{ label: "Menu Item One", value: "A0" },
							{ label: "Menu Item Two", value: "B0" },
							{ label: "Menu Item Three", value: "C0" },
							{ type: "divider" },
							{ label: "Menu Item Four", value: "D0" }
						]}
					>
						<DropdownTrigger>
							<Button
								assistiveText={{ icon: "Change view" }}
								iconCategory="utility"
								iconName="table"
								iconVariant="more"
								variant="icon"
							/>
						</DropdownTrigger>
					</Dropdown>
				</PageHeaderControl>
				{/* @ts-ignore */}
				{/*
				<PageHeaderControl>
					<Button
						assistiveText={{ icon: "Edit List" }}
						iconCategory="utility"
						iconName="edit"
						iconVariant="border-filled"
						variant="icon"
					/>
				</PageHeaderControl>
				*/}
				{
					hasMap &&
					/* @ts-ignore */
					<PageHeaderControl>
						<ButtonGroup variant="list">
							<Button
								assistiveText={{ icon: "Line" }}
								iconCategory="utility"
								iconName="rows"
								iconVariant={this.props.showMap ? "border-filled" : "brand"}
								variant="icon"
								className="slds-button_icon"
								onClick={() => this.props.onShowMap?.(false)}
							/>
							<Button
								assistiveText={{ icon: "Map" }}
								iconCategory="utility"
								iconName="location"
								iconVariant={this.props.showMap ? "brand" : "border-filled"}
								variant="icon"
								className="slds-button_icon"
								onClick={() => this.props.onShowMap?.(true)}
							/>
						</ButtonGroup>
					</PageHeaderControl>
				}
				{/* @ts-ignore */}
				<PageHeaderControl>
					<Button
						assistiveText={{ icon: "Refresh" }}
						iconCategory="utility"
						iconName="refresh"
						iconVariant="border-filled"
						variant="icon"
						onClick={() => onRefresh && onRefresh()}
					/>
				</PageHeaderControl>
			</>
		);

		const templateName =
			this.activeTemplateId &&
			(templateList?.find((t) => t.id === this.activeTemplateId)?.name || "<Please select ...>");
		const templateInfo = `${reportData?.data?.length || "0"} ${label}${this.modifiedAtText ? " • updated " + this.modifiedAtText : ""
			}`;
		const variant = "object-home";

		return (
			<ScrollablePageHeader
				element={document.getElementsByClassName("slds-brand-band").item(0) as HTMLDivElement}
				variant={variant}
			>
				{(classes: string) => (
					<PageHeader
						icon={<Icon assistiveText={{ label: label }} category={iconCategory} name={iconName} />}
						info={templateInfo}
						label={<span className="slds-text-title_caps">{label}</span>}
						nameSwitcherDropdown={
							<Dropdown
								assistiveText={{ icon: "Template Switcher" }}
								buttonClassName="slds-button_icon-small"
								buttonVariant="icon"
								iconCategory="utility"
								iconName="down"
								id="page-header-name-switcher-dropdown"
								checkmark
								options={templateList.map((t) => {
									return { value: t.id, label: t.name };
								})}
								onSelect={(template: { value: string; label: string }) => {
									this.activeTemplateId = template.value;
									onSelectTemplate && onSelectTemplate(this.activeTemplateId);
								}}
							/>
						}
						onRenderActions={actions}
						onRenderControls={controls}
						title={templateName}
						variant={variant}
						className={classes}
					/>
				)}
			</ScrollablePageHeader>
		);
	}

	private updateModifiedAtText() {
		this.modifiedAtText = this.props.modifiedAt ? moment(this.props.modifiedAt!).fromNow() : "";
	}
}
