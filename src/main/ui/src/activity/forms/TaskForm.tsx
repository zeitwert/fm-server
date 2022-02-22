
import Button from "@salesforce/design-system-react/components/button";
import Checkbox from "@salesforce/design-system-react/components/checkbox";
import Combobox from "@salesforce/design-system-react/components/combobox";
import Icon from "@salesforce/design-system-react/components/icon";
import Input from "@salesforce/design-system-react/components/input";
import Textarea from "@salesforce/design-system-react/components/textarea";
import Tooltip from "@salesforce/design-system-react/components/tooltip";
import { Account, Aggregate, API, Config, Enumerated, GenericUserType, UserInfo } from "@zeitwert/ui-model";
import Datepicker from "@zeitwert/ui-slds/common/Datepicker";
import { Col, Grid } from "@zeitwert/ui-slds/common/Grid";
import GenericUserCombobox, { ComboboxItem } from "@zeitwert/ui-slds/custom/GenericUserCombobox";
import { ActivityFormTypes, ActivityProps } from "activity/ActivityPortlet";
import { AppCtx } from "App";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import moment from "moment";
import React from "react";

/**
 * Id = number of minutes.
 */
const reminderOptions = [
	{
		id: 1440,
		label: "1 day"
	},
	{
		id: 2880,
		label: "2 days"
	},
	{
		id: 4320,
		label: "3 days"
	},
	{
		id: 5760,
		label: "4 days"
	},
	{
		id: 7200,
		label: "5 days"
	},
	{
		id: 8640,
		label: "6 days"
	},
	{
		id: 10080,
		label: "7 days"
	},
	{
		id: 20160,
		label: "2 weeks"
	},
	{
		id: 30240,
		label: "3 weeks"
	},
	{
		id: 40320,
		label: "4 weeks"
	}
];

export interface TaskFormState {
	name: string;
	description: string;
	priority?: Enumerated;
	dueDate?: Date;
	reminderSet: boolean;
	reminderOption?: ComboboxItem;
	refDoc?: string;
	refObj?: string;
	account?: Account;
	assignee?: ComboboxItem;
}

@inject("logger", "session")
@observer
export class TaskForm extends React.Component<ActivityProps, TaskFormState> {

	@observable priorities: Enumerated[] = [];
	@observable users: UserInfo[] = [];

	prioritiesOptions: any[] = [];

	get ctx() {
		return this.props as any as AppCtx;
	}

	get isValid() {
		return this.state.name && this.state.priority && this.state.dueDate;
	}

	get cleanState() {
		const { item, account } = this.props;
		return {
			name: "",
			description: "",
			refObj: item.isObj ? item.id : undefined,
			refDoc: item.isDoc ? item.id : undefined,
			account: account,
			priority: { id: "normal", name: "Normal" } as Enumerated,
			dueDate: moment().add(1, "day").toDate(),
			reminderSet: false,
			reminderOption: undefined,
			assignee: {
				id: this.ctx.session.sessionInfo!.user.id,
				label: this.ctx.session.sessionInfo!.user.caption,
				icon: <Icon category="standard" name="user" size="small" />,
				type: GenericUserType.User
			}
		};
	}

	constructor(props: ActivityProps) {
		super(props);
		makeObservable(this);
		this.state = this.cleanState;
	}

	async componentDidMount() {
		await this.loadTaskPriorities();
		this.prioritiesOptions = this.priorities.map((item: any) => ({
			id: item.id,
			label: item.name
		}));
	}

	render() {
		const { item, account, onSave } = this.props;
		const { name, description, dueDate, priority, reminderSet, reminderOption, assignee } = this.state;
		return (
			<>
				<Input
					label="Subject"
					onChange={(event: any) => this.setState({ name: event.target.value })}
					value={name}
					required
				/>
				<Textarea
					className="fa-textarea"
					label="Description"
					onChange={(event: any) => this.setState({ description: event.target.value })}
					value={description}
				/>
				<GenericUserCombobox
					labels={{
						label: "Assignee"
					}}
					account={account}
					items={assignee ? [assignee] : []}
					isRequired
					onChange={(items) => {
						this.setState({
							assignee: items ? items[0] : undefined
						});
					}}
				/>
				<Grid isVertical={false} className="slds-gutters_small">
					<Col className="slds-size_1-of-2">
						<Combobox
							labels={{
								label: "Priority"
							}}
							events={{
								onSelect: (event: any, data: any) => {
									if (!data.selection.length) {
										return;
									}
									this.setState({
										priority: this.priorities.find((p) => p.id === data.selection[0].id)
									});
								}
							}}
							selection={
								priority
									? [
										{
											id: priority.id,
											label: priority.name
										}
									]
									: undefined
							}
							options={this.prioritiesOptions}
							variant="readonly"
							required
						/>
					</Col>
					<Col className="slds-size_1-of-2">
						<Datepicker
							label="Due Date"
							value={dueDate}
							onChange={(date) => this.setState({ dueDate: date })}
							isRequired
							isOnlyFuture
						/>
					</Col>
				</Grid>
				<Grid isVertical={false}>
					<Col className="slds-size_4-of-12">
						<div className="slds-form-element">
							<label className="slds-form-element__label">Reminder Set</label>
							<Tooltip
								align="top left"
								content="Receive a reminder email at your preferred date and time, prior to Due Date"
								position="overflowBoundaryElement"
							/>
						</div>
						<Checkbox
							onChange={() => this.setState({ reminderSet: !reminderSet })}
							checked={!!reminderSet}
						/>
					</Col>
					<Col className="slds-size_8-of-12">
						{reminderSet && (
							<Combobox
								labels={{
									label: "Date"
								}}
								events={{
									onSelect: (event: any, data: any) => {
										this.setState({ reminderOption: data.selection?.[0] });
									}
								}}
								selection={[reminderOption]}
								options={reminderOptions}
								variant="readonly"
							/>
						)}
					</Col>
				</Grid>
				<Combobox
					labels={{
						label: "Related To"
					}}
					selection={[this.convertItem(item)]}
					options={[]}
					variant="inline-listbox"
					singleInputDisabled
				/>
				<Grid>
					<Col>
						<Button
							className="slds-m-top_medium slds-float_right"
							label="Add"
							disabled={!this.isValid}
							onClick={() => {
								onSave(ActivityFormTypes.TASK, this.state);
								this.setState(this.cleanState);
							}}
							variant="brand"
						/>
					</Col>
				</Grid>
			</>
		);
	}

	private async loadTaskPriorities() {
		try {
			const response = await API.get(Config.getEnumUrl("task", "codeTaskPriority"));
			this.priorities = response.data;
		} catch (error: any) {
			this.ctx.logger.error("Couldn't load task priorities", error);
		}
	}

	private convertItem(item: Aggregate) {
		return {
			id: item.id,
			label: item.caption,
			subTitle: item.owner.caption,
			icon: <Icon category={item.type.iconCategory} name={item.type.iconName} size="small" />
		};
	}

}
