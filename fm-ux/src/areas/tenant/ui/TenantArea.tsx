import type { ColumnType } from "antd/es/table";
import { useTranslation } from "react-i18next";
import { ItemsPage } from "../../../common/components/items";
import { canCreateEntity } from "../../../common/utils";
import { tenantListApi } from "../api";
import { tenantKeys } from "../queries";
import { TenantCreationForm } from "./forms/TenantCreationForm";
import { TenantPreview } from "./TenantPreview";
import type { TenantListItem } from "../types";
import { useSessionStore } from "@/session/model/sessionStore";
import { getArea } from "@/app/config/AppConfig";

export function TenantArea() {
	const { t } = useTranslation();
	const { sessionInfo } = useSessionStore();
	const userRole = sessionInfo?.user?.role?.id ?? "";
	const tenantType = sessionInfo?.tenant?.tenantType?.id ?? "";

	const columns: ColumnType<TenantListItem>[] = [
		{
			title: t("tenant:label.name"),
			dataIndex: "name",
			key: "name",
			sorter: (a, b) => a.name.localeCompare(b.name),
			defaultSortOrder: "ascend",
		},
		{
			title: t("tenant:label.tenantType"),
			dataIndex: ["tenantType", "name"],
			key: "tenantType",
			sorter: (a, b) => (a.tenantType?.name ?? "").localeCompare(b.tenantType?.name ?? ""),
		},
		{
			title: t("tenant:label.owner"),
			dataIndex: ["owner", "name"],
			key: "owner",
			sorter: (a, b) => (a.owner?.name ?? "").localeCompare(b.owner?.name ?? ""),
		},
	];

	return (
		<ItemsPage<TenantListItem>
			entityType="tenant"
			icon={getArea("tenant")?.icon}
			queryKey={[...tenantKeys.lists()]}
			queryFn={() => tenantListApi.list()}
			columns={columns}
			canCreate={canCreateEntity("tenant", userRole, tenantType)}
			CreateForm={TenantCreationForm}
			PreviewComponent={TenantPreview}
			getDetailPath={(record) => `/tenant/${record.id}`}
		/>
	);
}
