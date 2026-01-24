import type { ColumnType } from "antd/es/table";
import { useTranslation } from "react-i18next";
import { ItemsPage } from "../../../common/components/items";
import { canCreateEntity } from "../../../common/utils";
import { buildingListApi } from "../api";
import { buildingKeys } from "../queries";
import { BuildingCreationForm } from "./forms/BuildingCreationForm";
import { BuildingPreview } from "./BuildingPreview";
import type { BuildingListItem } from "../types";
import { useSessionStore } from "../../../session/model/sessionStore";
import { getArea } from "../../../app/config/AppConfig";

export function BuildingArea() {
	const { t } = useTranslation();
	const { sessionInfo } = useSessionStore();
	const userRole = sessionInfo?.user?.role?.id ?? "";
	const tenantType = sessionInfo?.tenant?.tenantType?.id ?? "";

	const columns: ColumnType<BuildingListItem>[] = [
		{
			title: t("building:label.buildingNr"),
			dataIndex: "buildingNr",
			key: "buildingNr",
			width: 100,
			sorter: (a, b) => (a.buildingNr ?? "").localeCompare(b.buildingNr ?? ""),
		},
		{
			title: t("building:label.name"),
			dataIndex: "name",
			key: "name",
			sorter: (a, b) => a.name.localeCompare(b.name),
			defaultSortOrder: "ascend",
		},
		{
			title: t("building:label.city"),
			dataIndex: "city",
			key: "city",
			sorter: (a, b) => (a.city ?? "").localeCompare(b.city ?? ""),
		},
		{
			title: t("building:label.owner"),
			dataIndex: ["owner", "name"],
			key: "owner",
			sorter: (a, b) => (a.owner?.name ?? "").localeCompare(b.owner?.name ?? ""),
		},
		{
			title: t("building:label.ratingStatus"),
			dataIndex: ["currentRating", "ratingStatus", "name"],
			key: "ratingStatus",
			sorter: (a, b) =>
				(a.currentRating?.ratingStatus?.name ?? "").localeCompare(
					b.currentRating?.ratingStatus?.name ?? ""
				),
		},
	];

	return (
		<ItemsPage<BuildingListItem>
			entityType="building"
			icon={getArea("building")?.icon}
			queryKey={[...buildingKeys.lists()]}
			queryFn={() => buildingListApi.list()}
			columns={columns}
			canCreate={canCreateEntity("building", userRole, tenantType)}
			CreateForm={BuildingCreationForm}
			PreviewComponent={BuildingPreview}
			getDetailPath={(record) => `/building/${record.id}`}
		/>
	);
}
