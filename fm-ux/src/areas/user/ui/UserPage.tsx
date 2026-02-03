import { Spin, Result, Tabs } from "antd";
import { useTranslation } from "react-i18next";
import { Link } from "@tanstack/react-router";
import { usePersistentForm } from "../../../common/hooks";
import { ItemPageHeader, ItemPageLayout, EditControls } from "../../../common/components/items";
import { AfForm } from "../../../common/components/form";
import { RelatedPanel } from "../../../common/components/related";
import { NotesList } from "../../../common/components/related/NotesList";
import { TasksList } from "../../../common/components/related/TasksList";
import { ActivityTimeline } from "../../../common/components/related/ActivityTimeline";
import type { Note } from "../../../common/components/related/NotesList";
import type { Task } from "../../../common/components/related/TasksList";
import type { Activity } from "../../../common/components/related/ActivityTimeline";
import { canModifyEntity } from "../../../common/utils";
import { useUserQuery, useUpdateUser } from "../queries";
import { userFormSchema, type UserFormInput } from "../schemas";
import { UserMainForm } from "./forms/UserMainForm";
import type { User } from "../types";
import { useSessionStore } from "../../../session/model/sessionStore";
import { getArea } from "../../../app/config/AppConfig";

interface UserPageProps {
	userId: string;
}

export function UserPage({ userId }: UserPageProps) {
	const { t } = useTranslation();
	const { sessionInfo } = useSessionStore();
	const userRole = sessionInfo?.user?.role?.id ?? "";

	const query = useUserQuery(userId);
	const updateMutation = useUpdateUser();

	const { form, isEditing, isDirty, isStoring, handleEdit, handleCancel, handleStore } =
		usePersistentForm<User, UserFormInput>({
			id: userId,
			data: query.data,
			updateMutation,
			schema: userFormSchema,
		});

	const user = query.data;

	if (query.isLoading) {
		return (
			<div className="af-loading-inline">
				<Spin size="large" />
			</div>
		);
	}

	if (query.isError || !user) {
		return (
			<Result
				status="404"
				title={t("user:message.notFound")}
				subTitle={t("user:message.notFoundDescription")}
				extra={<Link to="/user">{t("user:action.backToList")}</Link>}
			/>
		);
	}

	const canEdit = canModifyEntity("user", userRole);

	return (
		<div className="af-flex-column af-full-height">
			<ItemPageHeader
				icon={getArea("user")?.icon}
				title={user.name}
				details={[
					{
						label: t("user:label.email"),
						content: user.email,
					},
					{
						label: t("user:label.tenant"),
						content: user.tenant?.name,
					},
					{
						label: t("user:label.role"),
						content: user.role?.name,
					},
				]}
			/>

			<ItemPageLayout
				rightPanel={
					<RelatedPanel
						sections={[
							{
								key: "notes",
								label: t("user:label.notes"),
								children: <NotesList notes={[] as Note[]} />,
							},
							{
								key: "tasks",
								label: t("user:label.tasks"),
								children: <TasksList tasks={[] as Task[]} />,
							},
							{
								key: "activity",
								label: t("user:label.activity"),
								children: <ActivityTimeline activities={[] as Activity[]} />,
							},
						]}
					/>
				}
			>
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
								label: t("user:label.tabMain"),
								children: <UserMainForm disabled={!isEditing} />,
							},
						]}
					/>
				</AfForm>
			</ItemPageLayout>
		</div>
	);
}
