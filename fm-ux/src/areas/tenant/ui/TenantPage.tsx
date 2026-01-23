import { Card, Spin, Result, Tabs } from "antd";
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
import { useTenantQuery, useUpdateTenant } from "../queries";
import { tenantFormSchema, type TenantFormInput } from "../schemas";
import { TenantMainForm } from "./forms/TenantMainForm";
import type { Tenant } from "../types";
import { useSessionStore } from "../../../session/model/sessionStore";
import { getArea } from "../../../app/config/AppConfig";

interface TenantPageProps {
	tenantId: string;
}

export function TenantPage({ tenantId }: TenantPageProps) {
	const { t } = useTranslation();
	const { sessionInfo } = useSessionStore();
	const userRole = sessionInfo?.user?.role?.id ?? "";

	const query = useTenantQuery(tenantId);
	const updateMutation = useUpdateTenant();

	const { form, isEditing, isDirty, isStoring, handleEdit, handleCancel, handleStore } =
		usePersistentForm<Tenant, TenantFormInput>({
			id: tenantId,
			data: query.data,
			updateMutation,
			schema: tenantFormSchema,
		});

	const tenant = query.data;

	if (query.isLoading) {
		return (
			<div className="af-loading-inline">
				<Spin size="large" />
			</div>
		);
	}

	if (query.isError || !tenant) {
		return (
			<Result
				status="404"
				title={t("tenant:message.notFound")}
				subTitle={t("tenant:message.notFoundDescription")}
				extra={<Link to="/tenant">{t("tenant:action.backToList")}</Link>}
			/>
		);
	}

	const canEdit = canModifyEntity("tenant", userRole);

	return (
		<div className="af-flex-column af-full-height">
			<ItemPageHeader
				icon={getArea("tenant")?.icon}
				title={tenant.name}
				details={[
					{
						label: t("tenant:label.tenantType"),
						content: tenant.tenantType?.name,
					},
					{
						label: t("tenant:label.owner"),
						content: tenant.owner?.name,
					},
				]}
			/>

			<ItemPageLayout
				rightPanel={
					<RelatedPanel
						sections={[
							{
								key: "notes",
								label: t("tenant:label.notes"),
								children: <NotesList notes={[] as Note[]} />,
							},
							{
								key: "tasks",
								label: t("tenant:label.tasks"),
								children: <TasksList tasks={[] as Task[]} />,
							},
							{
								key: "activity",
								label: t("tenant:label.activity"),
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
									label: t("tenant:label.tabMain"),
									children: <TenantMainForm disabled={!isEditing} />,
								},
							]}
						/>
					</AfForm>
				</Card>
			</ItemPageLayout>
		</div>
	);
}
