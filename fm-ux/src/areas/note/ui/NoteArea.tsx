import type { ColumnType } from "antd/es/table";
import { useTranslation } from "react-i18next";
import { ItemsPage } from "../../../common/components/items";
import { noteListApi } from "../api";
import { noteKeys } from "../queries";
import { NotePreview } from "./NotePreview";
import type { NoteListItem } from "../types";
import { getArea } from "../../../app/config/AppConfig";

export function NoteArea() {
	const { t } = useTranslation();

	const columns: ColumnType<NoteListItem>[] = [
		{
			title: t("note:label.subject"),
			dataIndex: "subject",
			key: "subject",
			sorter: (a, b) => (a.subject ?? "").localeCompare(b.subject ?? ""),
			render: (text) => text || t("common:label.noTitle"),
		},
		{
			title: t("note:label.noteType"),
			dataIndex: ["noteType", "name"],
			key: "noteType",
			sorter: (a, b) => (a.noteType?.name ?? "").localeCompare(b.noteType?.name ?? ""),
		},
		{
			title: t("note:label.relatedTo"),
			dataIndex: ["relatedTo", "name"],
			key: "relatedTo",
			sorter: (a, b) => (a.relatedTo?.name ?? "").localeCompare(b.relatedTo?.name ?? ""),
		},
		{
			title: t("note:label.owner"),
			dataIndex: ["owner", "name"],
			key: "owner",
			sorter: (a, b) => (a.owner?.name ?? "").localeCompare(b.owner?.name ?? ""),
		},
		{
			title: t("note:label.modifiedAt"),
			key: "modifiedAt",
			render: (_, record) => {
				if (!record.meta?.modifiedAt) return "-";
				return new Date(record.meta.modifiedAt as string).toLocaleDateString("de-CH", {
					day: "2-digit",
					month: "2-digit",
					year: "numeric",
				});
			},
			sorter: (a, b) => {
				if (!a.meta?.modifiedAt) return 1;
				if (!b.meta?.modifiedAt) return -1;
				const aDate = new Date(a.meta.modifiedAt as string).getTime();
				const bDate = new Date(b.meta.modifiedAt as string).getTime();
				return bDate - aDate;
			},
			defaultSortOrder: "ascend",
		},
	];

	return (
		<ItemsPage<NoteListItem>
			entityType="note"
			entityLabelKey="note.label.entityCount"
			entityLabelSingular={t("note:label.entity")}
			icon={getArea("note")?.icon}
			queryKey={[...noteKeys.lists()]}
			queryFn={() => noteListApi.list()}
			columns={columns}
			canCreate={false}
			PreviewComponent={NotePreview}
			getDetailPath={(record) => `/note/${record.id}`}
		/>
	);
}
