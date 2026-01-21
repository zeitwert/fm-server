import { TeamOutlined } from "@ant-design/icons";
import type { ColumnType } from "antd/es/table";
import { useTranslation } from "react-i18next";
import { ItemsPage } from "../../../common/components/items";
import { contactListApi } from "../api";
import { contactKeys } from "../queries";
import { ContactPreview } from "./ContactPreview";
import type { ContactListItem } from "../types";

export function ContactArea() {
	const { t } = useTranslation("contact");

	const columns: ColumnType<ContactListItem>[] = [
		{
			title: t("name"),
			dataIndex: "caption",
			key: "name",
			sorter: (a, b) => (a.caption ?? "").localeCompare(b.caption ?? ""),
			defaultSortOrder: "ascend",
		},
		{
			title: t("account"),
			dataIndex: ["account", "caption"],
			key: "account",
			sorter: (a, b) => (a.account?.caption ?? "").localeCompare(b.account?.caption ?? ""),
		},
		{
			title: t("email"),
			dataIndex: "email",
			key: "email",
		},
		{
			title: t("phone"),
			key: "phone",
			render: (_, record) => record.mobile || record.phone,
		},
		{
			title: t("owner"),
			dataIndex: ["owner", "name"],
			key: "owner",
			sorter: (a, b) => (a.owner?.name ?? "").localeCompare(b.owner?.name ?? ""),
		},
	];

	return (
		<ItemsPage<ContactListItem>
			entityType="contact"
			entityLabel={t("contacts")}
			entityLabelSingular={t("contact")}
			icon={<TeamOutlined />}
			queryKey={[...contactKeys.lists()]}
			queryFn={() => contactListApi.list()}
			columns={columns}
			canCreate={false}
			PreviewComponent={ContactPreview}
			getDetailPath={(record) => `/contact/${record.id}`}
		/>
	);
}
