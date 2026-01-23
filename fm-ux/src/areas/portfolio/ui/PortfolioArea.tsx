import type { ColumnType } from "antd/es/table";
import { useTranslation } from "react-i18next";
import { ItemsPage } from "../../../common/components/items";
import { canCreateEntity } from "../../../common/utils";
import { portfolioListApi } from "../api";
import { portfolioKeys } from "../queries";
import { PortfolioCreationForm } from "./forms/PortfolioCreationForm";
import { PortfolioPreview } from "./PortfolioPreview";
import type { PortfolioListItem } from "../types";
import { useSessionStore } from "../../../session/model/sessionStore";
import { getArea } from "../../../app/config/AppConfig";

export function PortfolioArea() {
	const { t } = useTranslation();
	const { sessionInfo } = useSessionStore();
	const userRole = sessionInfo?.user?.role?.id ?? "";
	const tenantType = sessionInfo?.tenant?.tenantType?.id ?? "";

	const columns: ColumnType<PortfolioListItem>[] = [
		{
			title: t("portfolio:label.name"),
			dataIndex: "name",
			key: "name",
			sorter: (a, b) => a.name.localeCompare(b.name),
			defaultSortOrder: "ascend",
		},
		{
			title: t("portfolio:label.account"),
			dataIndex: ["account", "name"],
			key: "account",
			sorter: (a, b) => (a.account?.name ?? "").localeCompare(b.account?.name ?? ""),
		},
		{
			title: t("portfolio:label.owner"),
			dataIndex: ["owner", "name"],
			key: "owner",
			sorter: (a, b) => (a.owner?.name ?? "").localeCompare(b.owner?.name ?? ""),
		},
	];

	return (
		<ItemsPage<PortfolioListItem>
			entityType="portfolio"
			icon={getArea("portfolio")?.icon}
			queryKey={[...portfolioKeys.lists()]}
			queryFn={() => portfolioListApi.list()}
			columns={columns}
			canCreate={canCreateEntity("portfolio", userRole, tenantType)}
			CreateForm={PortfolioCreationForm}
			PreviewComponent={PortfolioPreview}
			getDetailPath={(record) => `/portfolio/${record.id}`}
		/>
	);
}
