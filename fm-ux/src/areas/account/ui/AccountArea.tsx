import { BankOutlined } from "@ant-design/icons";
import type { ColumnType } from "antd/es/table";
import { useTranslation } from "react-i18next";
import { ItemsPage } from "../../../common/components/items";
import { accountListApi } from "../api";
import { accountKeys } from "../queries";
import { AccountCreationForm } from "./forms/AccountCreationForm";
import { AccountPreview } from "./AccountPreview";
import type { AccountListItem } from "../types";
import { useSessionStore } from "../../../session/model/sessionStore";
import { ROLE_ADMIN, ROLE_APP_ADMIN, ROLE_SUPER_USER } from "../../../session/model/types";

function canCreateAccount(role?: string): boolean {
	return role === ROLE_ADMIN || role === ROLE_APP_ADMIN || role === ROLE_SUPER_USER;
}

export function AccountArea() {
	const { t } = useTranslation();
	const { sessionInfo } = useSessionStore();
	const userRole = sessionInfo?.user?.role?.id;

	const columns: ColumnType<AccountListItem>[] = [
		{
			title: t("account:label.name"),
			dataIndex: "name",
			key: "name",
			sorter: (a, b) => a.name.localeCompare(b.name),
			defaultSortOrder: "ascend",
		},
		{
			title: t("account:label.accountType"),
			dataIndex: ["accountType", "name"],
			key: "accountType",
			sorter: (a, b) => (a.accountType?.name ?? "").localeCompare(b.accountType?.name ?? ""),
		},
		{
			title: t("account:label.tenant"),
			dataIndex: ["tenant", "name"],
			key: "tenant",
			sorter: (a, b) => (a.tenant?.name ?? "").localeCompare(b.tenant?.name ?? ""),
		},
		{
			title: t("account:label.mainContact"),
			dataIndex: ["mainContact", "caption"],
			key: "mainContact",
			sorter: (a, b) => (a.mainContact?.caption ?? "").localeCompare(b.mainContact?.caption ?? ""),
		},
		{
			title: t("account:label.contactEmail"),
			dataIndex: ["mainContact", "email"],
			key: "email",
		},
		{
			title: t("account:label.contactPhone"),
			key: "phone",
			render: (_, record) => record.mainContact?.mobile || record.mainContact?.phone,
		},
		{
			title: t("account:label.owner"),
			dataIndex: ["owner", "name"],
			key: "owner",
			sorter: (a, b) => (a.owner?.name ?? "").localeCompare(b.owner?.name ?? ""),
		},
	];

	return (
		<ItemsPage<AccountListItem>
			entityType="account"
			entityLabelKey="account.label.entityCount"
			entityLabelSingular={t("account:label.entity")}
			icon={<BankOutlined />}
			queryKey={[...accountKeys.lists()]}
			queryFn={() => accountListApi.list()}
			columns={columns}
			canCreate={canCreateAccount(userRole)}
			CreateForm={AccountCreationForm}
			PreviewComponent={AccountPreview}
			getDetailPath={(record) => `/account/${record.id}`}
		/>
	);
}
