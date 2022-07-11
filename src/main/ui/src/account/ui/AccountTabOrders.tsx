import { Account, ItemListModel } from "@zeitwert/ui-model";
import { DataTableCellWithEntityIcon, DataTableCellWithLink } from "@zeitwert/ui-slds/custom/CustomDataTableCells";
import { ReportViewer } from "@zeitwert/ui-slds/report/ReportViewer";
import { AppCtx } from "frame/App";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

const DOC_DATAMART = "doc.docs";

interface AccountTabOrdersProps {
	account: Account;
	template: string;
}

@inject("logger", "showToast")
@observer
export default class AccountTabOrders extends React.Component<AccountTabOrdersProps> {
	@observable docListStore = ItemListModel.create({ datamart: DOC_DATAMART });

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: AccountTabOrdersProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		try {
			await this.docListStore.initTemplates();
			await this.docListStore.executeTemplate(this.props.template, {
				accountId: this.props.account.id
			});
		} catch (error: any) {
			this.ctx.logger.error("Could not load doc list", error);
			this.ctx.showToast("warning", "Could not load doc list");
		}
	}

	render() {
		if (!this.docListStore.template?.layout) {
			return <></>;
		}
		return (
			<ReportViewer
				layout={this.docListStore.template?.layout}
				data={this.docListStore.reportData}
				options={{ fixedHeader: false }}
				templates={{
					link: DataTableCellWithLink,
					entityIcon: DataTableCellWithEntityIcon
				}}
			/>
		);
	}
}
