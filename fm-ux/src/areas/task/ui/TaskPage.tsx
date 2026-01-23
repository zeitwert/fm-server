import { Card, Spin, Result, Tabs } from "antd";
import { useTranslation } from "react-i18next";
import { Link } from "@tanstack/react-router";
import { useEditableEntity } from "../../../common/hooks/useEditableEntity";
import { DocPageHeader } from "../../../common/components/doc";
import { ItemPageLayout, EditControls } from "../../../common/components/items";
import { AfForm } from "../../../common/components/form";
import { RelatedPanel } from "../../../common/components/related";
import { NotesList } from "../../../common/components/related/NotesList";
import { ActivityTimeline } from "../../../common/components/related/ActivityTimeline";
import type { Note } from "../../../common/components/related/NotesList";
import type { Activity } from "../../../common/components/related/ActivityTimeline";
import { canModifyEntity } from "../../../common/utils";
import { taskApi } from "../api";
import { taskKeys } from "../queries";
import { taskFormSchema, type TaskFormInput } from "../schemas";
import { TaskMainForm } from "./forms/TaskMainForm";
import type { Task, CaseStage } from "../types";
import { useSessionStore } from "../../../session/model/sessionStore";
import { getArea } from "../../../app/config/AppConfig";

interface TaskPageProps {
	taskId: string;
}

export function TaskPage({ taskId }: TaskPageProps) {
	const { t } = useTranslation();
	const { sessionInfo } = useSessionStore();
	const userRole = sessionInfo?.user?.role?.id ?? "";

	const {
		entity: task,
		form,
		isLoading,
		isError,
		isEditing,
		isDirty,
		isStoring,
		handleEdit,
		handleCancel,
		handleStore,
		directMutation,
	} = useEditableEntity<Task, TaskFormInput>({
		id: taskId,
		queryKey: taskKeys.details(),
		queryFn: (id) => taskApi.get(id),
		updateFn: taskApi.update,
		schema: taskFormSchema,
		listQueryKey: taskKeys.lists(),
	});

	const handleStageTransition = async (stage: CaseStage) => {
		await directMutation({ caseStage: stage });
	};

	if (isLoading) {
		return (
			<div className="af-loading-inline">
				<Spin size="large" />
			</div>
		);
	}

	if (isError || !task) {
		return (
			<Result
				status="404"
				title={t("task:message.notFound")}
				subTitle={t("task:message.notFoundDescription")}
				extra={<Link to="/task">{t("task:action.backToList")}</Link>}
			/>
		);
	}

	const canEdit = canModifyEntity("task", userRole);

	return (
		<div className="af-flex-column af-full-height">
			<DocPageHeader
				icon={getArea("task")?.icon}
				title={task.subject || t("common:label.noTitle")}
				details={[
					{
						label: t("task:label.assignee"),
						content: task.meta?.assignee?.name,
					},
					{
						label: t("task:label.owner"),
						content: task.owner?.name,
					},
					{
						label: t("task:label.dueAt"),
						content: task.dueAt
							? new Date(task.dueAt).toLocaleDateString("de-CH", {
									day: "2-digit",
									month: "2-digit",
									year: "numeric",
								})
							: undefined,
					},
					{
						label: t("task:label.priority"),
						content: task.priority?.name,
					},
				]}
				currentStage={task.meta?.caseStage}
				stages={task.meta?.caseStages}
				onTransition={handleStageTransition}
				isEditing={isEditing}
			/>

			<ItemPageLayout
				rightPanel={
					<RelatedPanel
						sections={[
							{
								key: "notes",
								label: t("task:label.notes"),
								children: <NotesList notes={[] as Note[]} />,
							},
							{
								key: "activity",
								label: t("task:label.activity"),
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
									label: t("task:label.tabMain"),
									children: <TaskMainForm disabled={!isEditing} />,
								},
							]}
						/>
					</AfForm>
				</Card>
			</ItemPageLayout>
		</div>
	);
}
