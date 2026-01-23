import type { ColumnType } from "antd/es/table";
import { useTranslation } from "react-i18next";
import { ItemsPage } from "../../../common/components/items";
import { canCreateEntity } from "../../../common/utils";
import { contactListApi } from "../api";
import { contactKeys } from "../queries";
import { ContactCreationForm } from "./forms/ContactCreationForm";
import { ContactPreview } from "./ContactPreview";
import type { ContactListItem } from "../types";
import { useSessionStore } from "../../../session/model/sessionStore";
import { getArea } from "../../../app/config/AppConfig";

export function ContactArea() {
	const { t } = useTranslation();
	const { sessionInfo } = useSessionStore();
	const userRole = sessionInfo?.user?.role?.id ?? "";
	const tenantType = sessionInfo?.tenant?.tenantType?.id ?? "";
	// Can create contacts if session has an account context
	const canCreate = canCreateEntity("contact", userRole, tenantType) && !!sessionInfo?.account;

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
			icon={getArea("contact")?.icon}
			queryKey={[...contactKeys.lists()]}
			queryFn={() => contactListApi.list()}
			columns={columns}
			canCreate={canCreate}
			CreateForm={
				sessionInfo?.account
					? (props) => (
							<ContactCreationForm
								{...props}
								account={{ id: sessionInfo.account!.id, name: sessionInfo.account!.name }}
							/>
						)
					: undefined
			}
			PreviewComponent={ContactPreview}
			getDetailPath={(record) => `/contact/${record.id}`}
		/>
	);
}
