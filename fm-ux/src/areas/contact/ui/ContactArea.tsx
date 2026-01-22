import { TeamOutlined } from "@ant-design/icons";
import type { ColumnType } from "antd/es/table";
import { useTranslation } from "react-i18next";
import { ItemsPage } from "../../../common/components/items";
import { contactListApi } from "../api";
import { contactKeys } from "../queries";
import { ContactPreview } from "./ContactPreview";
import type { ContactListItem } from "../types";

export function ContactArea() {
	const { t } = useTranslation();

	const columns: ColumnType<ContactListItem>[] = [
		{
			title: t("contact:label.name"),
			dataIndex: "caption",
			key: "name",
			sorter: (a, b) => (a.caption ?? "").localeCompare(b.caption ?? ""),
			defaultSortOrder: "ascend",
		},
		{
			title: t("contact:label.account"),
			dataIndex: ["account", "caption"],
			key: "account",
			sorter: (a, b) => (a.account?.caption ?? "").localeCompare(b.account?.caption ?? ""),
		},
		{
			title: t("contact:label.email"),
			dataIndex: "email",
			key: "email",
		},
		{
			title: t("contact:label.phone"),
			key: "phone",
			render: (_, record) => record.mobile || record.phone,
		},
		{
			title: t("contact:label.owner"),
			dataIndex: ["owner", "name"],
			key: "owner",
			sorter: (a, b) => (a.owner?.name ?? "").localeCompare(b.owner?.name ?? ""),
		},
	];

	return (
		<ItemsPage<ContactListItem>
			entityType="contact"
			entityLabelKey="contact.label.entityCount"
			entityLabelSingular={t("contact:label.entity")}
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
