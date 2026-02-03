import type { ColumnType } from "antd/es/table";
import { useTranslation } from "react-i18next";
import { ItemsPage } from "../../../common/components/items";
import { taskListApi } from "../api";
import { taskKeys } from "../queries";
import { TaskPreview } from "./TaskPreview";
import type { TaskListItem } from "../types";
import { getArea } from "@/app/config/AppConfig";

export function TaskArea() {
	const { t } = useTranslation();

	const columns: ColumnType<TaskListItem>[] = [
		{
			title: t("task:label.subject"),
			dataIndex: "subject",
			key: "subject",
			sorter: (a, b) => a.subject.localeCompare(b.subject),
			defaultSortOrder: "ascend",
		},
		{
			title: t("task:label.relatedTo"),
			dataIndex: ["relatedTo", "name"],
			key: "relatedTo",
			sorter: (a, b) => (a.relatedTo?.name ?? "").localeCompare(b.relatedTo?.name ?? ""),
		},
		{
			title: t("task:label.stage"),
			dataIndex: ["meta", "caseStage", "name"],
			key: "stage",
			sorter: (a, b) =>
				(a.meta?.caseStage?.name ?? "").localeCompare(b.meta?.caseStage?.name ?? ""),
		},
		{
			title: t("task:label.priority"),
			dataIndex: ["priority", "name"],
			key: "priority",
			sorter: (a, b) => (a.priority?.name ?? "").localeCompare(b.priority?.name ?? ""),
		},
		{
			title: t("task:label.dueAt"),
			dataIndex: "dueAt",
			key: "dueAt",
			render: (date: string | undefined) => {
				if (!date) return "-";
				return new Date(date).toLocaleDateString("de-CH", {
					day: "2-digit",
					month: "2-digit",
					year: "numeric",
				});
			},
			sorter: (a, b) => {
				if (!a.dueAt) return 1;
				if (!b.dueAt) return -1;
				return new Date(a.dueAt).getTime() - new Date(b.dueAt).getTime();
			},
		},
		{
			title: t("task:label.remindAt"),
			dataIndex: "remindAt",
			key: "remindAt",
			render: (date: string | undefined) => {
				if (!date) return "-";
				return new Date(date).toLocaleDateString("de-CH", {
					day: "2-digit",
					month: "2-digit",
					year: "numeric",
				});
			},
			sorter: (a, b) => {
				if (!a.remindAt) return 1;
				if (!b.remindAt) return -1;
				return new Date(a.remindAt).getTime() - new Date(b.remindAt).getTime();
			},
		},
		{
			title: t("task:label.assignee"),
			dataIndex: ["meta", "assignee", "name"],
			key: "assignee",
			sorter: (a, b) => (a.meta?.assignee?.name ?? "").localeCompare(b.meta?.assignee?.name ?? ""),
		},
	];

	return (
		<ItemsPage<TaskListItem>
			entityType="task"
			icon={getArea("task")?.icon}
			queryKey={[...taskKeys.lists()]}
			queryFn={() => taskListApi.list()}
			columns={columns}
			canCreate={false}
			PreviewComponent={TaskPreview}
			getDetailPath={(record) => `/task/${record.id}`}
		/>
	);
}
