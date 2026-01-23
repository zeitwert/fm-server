import { Card, Spin, Result, Tabs } from "antd";
import { useTranslation } from "react-i18next";
import { Link } from "@tanstack/react-router";
import { useEntityQueries } from "../../../common/hooks/useEntityQueries";
import { ItemPageHeader, ItemPageLayout, EditControls } from "../../../common/components/items";
import { AfForm } from "../../../common/components/form";
import { RelatedPanel } from "../../../common/components/related";
import { ActivityTimeline } from "../../../common/components/related/ActivityTimeline";
import type { Activity } from "../../../common/components/related/ActivityTimeline";
import { canModifyEntity } from "../../../common/utils";
import { noteApi } from "../api";
import { noteKeys } from "../queries";
import { noteFormSchema, type NoteFormInput } from "../schemas";
import { NoteMainForm } from "./forms/NoteMainForm";
import type { Note } from "../types";
import { useSessionStore } from "../../../session/model/sessionStore";
import { getArea } from "../../../app/config/AppConfig";

interface NotePageProps {
	noteId: string;
}

export function NotePage({ noteId }: NotePageProps) {
	const { t } = useTranslation();
	const { sessionInfo } = useSessionStore();
	const userRole = sessionInfo?.user?.role?.id ?? "";

	const {
		entity: note,
		form,
		isLoading,
		isError,
		isEditing,
		isDirty,
		isStoring,
		handleEdit,
		handleCancel,
		handleStore,
	} = useEntityQueries<Note, NoteFormInput>({
		id: noteId,
		queryKey: noteKeys.details(),
		queryFn: (id) => noteApi.get(id),
		updateFn: noteApi.update,
		schema: noteFormSchema,
		listQueryKey: noteKeys.lists(),
	});

	if (isLoading) {
		return (
			<div className="af-loading-inline">
				<Spin size="large" />
			</div>
		);
	}

	if (isError || !note) {
		return (
			<Result
				status="404"
				title={t("note:message.notFound")}
				subTitle={t("note:message.notFoundDescription")}
				extra={<Link to="/note">{t("note:action.backToList")}</Link>}
			/>
		);
	}

	const canEdit = canModifyEntity("note", userRole);

	return (
		<div className="af-flex-column af-full-height">
			<ItemPageHeader
				icon={getArea("note")?.icon}
				title={note.subject || t("common:label.noTitle")}
				details={[
					{
						label: t("note:label.noteType"),
						content: note.noteType?.name,
					},
					{
						label: t("note:label.relatedTo"),
						content: note.relatedTo?.name,
					},
					{
						label: t("note:label.tenant"),
						content: note.tenant?.name,
					},
					{
						label: t("note:label.owner"),
						content: note.owner?.name,
					},
				]}
			/>

			<ItemPageLayout
				rightPanel={
					<RelatedPanel
						sections={[
							{
								key: "activity",
								label: t("note:label.activity"),
								children: <ActivityTimeline activities={[] as Activity[]} />,
							},
						]}
					/>
				}
			>
				<Card className="af-full-height">
					<AfForm form={form}>
						<Tabs
							tabBarExtraContent={
								<EditControls
									isEditing={isEditing}
									isDirty={isDirty}
									isStoring={isStoring}
									canEdit={canEdit}
									onEdit={handleEdit}
									onCancel={handleCancel}
									onStore={handleStore}
								/>
							}
							items={[
								{
									key: "main",
									label: t("note:label.tabMain"),
									children: <NoteMainForm disabled={!isEditing} />,
								},
							]}
						/>
					</AfForm>
				</Card>
			</ItemPageLayout>
		</div>
	);
}
