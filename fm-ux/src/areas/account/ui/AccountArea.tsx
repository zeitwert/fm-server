/**
 * AccountArea - List view for accounts using ItemsPage.
 *
 * Displays a table of accounts with:
 * - Create button for new accounts
 * - Click-to-navigate to account detail page
 * - Searchable table columns
 */

import { BankOutlined } from "@ant-design/icons";
import type { ColumnType } from "antd/es/table";
import { useTranslation } from "react-i18next";
import { ItemsPage } from "../../../common/components/items";
import { accountListApi } from "../api";
import { accountKeys } from "../queries";
import { AccountCreationForm } from "./forms/AccountCreationForm";
import type { AccountListItem } from "../types";
import { useSessionStore } from "../../../session/model/sessionStore";
import { ROLE_ADMIN, ROLE_APP_ADMIN, ROLE_SUPER_USER } from "../../../session/model/types";

/**
 * Check if user can create accounts.
 * From fm-ui: session.isAdmin && (session.isKernelTenant || session.isAdvisorTenant)
 * Simplified for now: admin or super_user roles can create
 */
function canCreateAccount(role?: string): boolean {
	return role === ROLE_ADMIN || role === ROLE_APP_ADMIN || role === ROLE_SUPER_USER;
}

export function AccountArea() {
	const { t } = useTranslation("account");
	const { sessionInfo } = useSessionStore();
	const userRole = sessionInfo?.user?.role?.id;

	const columns: ColumnType<AccountListItem>[] = [
		{
			title: t("name"),
			dataIndex: "name",
			key: "name",
			sorter: (a, b) => a.name.localeCompare(b.name),
			defaultSortOrder: "ascend",
		},
		{
			title: t("accountType"),
			dataIndex: ["accountType", "name"],
			key: "accountType",
			sorter: (a, b) => (a.accountType?.name ?? "").localeCompare(b.accountType?.name ?? ""),
		},
		{
			title: t("clientSegment"),
			dataIndex: ["clientSegment", "name"],
			key: "clientSegment",
			sorter: (a, b) => (a.clientSegment?.name ?? "").localeCompare(b.clientSegment?.name ?? ""),
		},
		{
			title: t("owner"),
			dataIndex: ["owner", "name"],
			key: "owner",
			sorter: (a, b) => (a.owner?.name ?? "").localeCompare(b.owner?.name ?? ""),
		},
		{
			title: t("mainContact"),
			dataIndex: ["mainContact", "name"],
			key: "mainContact",
		},
	];

	return (
		<ItemsPage<AccountListItem>
			entityType="account"
			entityLabel={t("accounts")}
			entityLabelSingular={t("account")}
			icon={<BankOutlined />}
			queryKey={[...accountKeys.lists()]}
			queryFn={() => accountListApi.list()}
			columns={columns}
			canCreate={canCreateAccount(userRole)}
			CreateForm={AccountCreationForm}
			getDetailPath={(record) => `/account/${record.id}`}
		/>
	);
}
