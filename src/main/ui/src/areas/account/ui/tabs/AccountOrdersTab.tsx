
import { Account, ItemListModel } from "@zeitwert/ui-model";
import { DataTableCellWithEntityIcon, DataTableCellWithLink, ReportViewer } from "@zeitwert/ui-slds";
import { AppCtx } from "app/App";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

const DOC_DATAMART = "doc.docs";

interface AccountTabOrdersProps {
	account: Account;
	template: string;
}

@inject("showToast")
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
			console.error("Could not load doc list", error);
			this.ctx.showToast("error", "Could not load doc list");
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
				dataTableCellTemplates={{
					link: DataTableCellWithLink,
					entityIcon: DataTableCellWithEntityIcon
				}}
			/>
		);
	}
}
