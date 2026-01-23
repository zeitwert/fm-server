import type { ColumnType } from "antd/es/table";
import { useTranslation } from "react-i18next";
import { ItemsPage } from "../../../common/components/items";
import { canCreateEntity } from "../../../common/utils";
import { userListApi } from "../api";
import { userKeys } from "../queries";
import { UserCreationForm } from "./forms/UserCreationForm";
import { UserPreview } from "./UserPreview";
import type { UserListItem } from "../types";
import { useSessionStore } from "../../../session/model/sessionStore";
import { getArea } from "../../../app/config/AppConfig";

export function UserArea() {
	const { t } = useTranslation();
	const { sessionInfo } = useSessionStore();
	const userRole = sessionInfo?.user?.role?.id ?? "";
	const tenantType = sessionInfo?.tenant?.tenantType?.id ?? "";

	const columns: ColumnType<UserListItem>[] = [
		{
			title: t("user:label.name"),
			dataIndex: "name",
			key: "name",
			sorter: (a, b) => a.name.localeCompare(b.name),
			defaultSortOrder: "ascend",
		},
		{
			title: t("user:label.email"),
			dataIndex: "email",
			key: "email",
			sorter: (a, b) => a.email.localeCompare(b.email),
		},
		{
			title: t("user:label.role"),
			dataIndex: ["role", "name"],
			key: "role",
			sorter: (a, b) => (a.role?.name ?? "").localeCompare(b.role?.name ?? ""),
		},
		{
			title: t("user:label.tenant"),
			dataIndex: ["tenant", "name"],
			key: "tenant",
			sorter: (a, b) => (a.tenant?.name ?? "").localeCompare(b.tenant?.name ?? ""),
		},
		{
			title: t("user:label.owner"),
			dataIndex: ["owner", "name"],
			key: "owner",
			sorter: (a, b) => (a.owner?.name ?? "").localeCompare(b.owner?.name ?? ""),
		},
	];

	return (
		<ItemsPage<UserListItem>
			entityType="user"
			icon={getArea("user")?.icon}
			queryKey={[...userKeys.lists()]}
			queryFn={() => userListApi.list()}
			columns={columns}
			canCreate={canCreateEntity("user", userRole, tenantType)}
			CreateForm={UserCreationForm}
			PreviewComponent={UserPreview}
			getDetailPath={(record) => `/user/${record.id}`}
		/>
	);
}
