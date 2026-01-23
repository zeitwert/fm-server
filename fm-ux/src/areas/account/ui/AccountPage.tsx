import { useState } from "react";
import { Button, Card, Modal, Spin, Result, Tabs } from "antd";
import { useTranslation } from "react-i18next";
import { Link } from "@tanstack/react-router";
import { useEntityQueries } from "../../../common/hooks/useEntityQueries";
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
import { accountApi } from "../api";
import { accountKeys } from "../queries";
import { accountFormSchema, type AccountFormInput } from "../schemas";
import { AccountMainForm } from "./forms/AccountMainForm";
import { ContactCreationForm } from "../../contact/ui/forms/ContactCreationForm";
import type { Account } from "../types";
import { useSessionStore } from "../../../session/model/sessionStore";
import { getArea } from "../../../app/config/AppConfig";

interface AccountPageProps {
	accountId: string;
}

export function AccountPage({ accountId }: AccountPageProps) {
	const { t } = useTranslation();
	const { sessionInfo } = useSessionStore();
	const userRole = sessionInfo?.user?.role?.id ?? "";
	const [isContactCreateOpen, setIsContactCreateOpen] = useState(false);

	const {
		entity: account,
		form,
		isLoading,
		isError,
		isEditing,
		isDirty,
		isStoring,
		handleEdit,
		handleCancel,
		handleStore,
	} = useEntityQueries<Account, AccountFormInput>({
		id: accountId,
		queryKey: accountKeys.details(),
		queryFn: (id) => accountApi.get(id),
		updateFn: accountApi.update,
		schema: accountFormSchema,
		listQueryKey: accountKeys.lists(),
	});

	if (isLoading) {
		return (
			<div className="af-loading-inline">
				<Spin size="large" />
			</div>
		);
	}

	if (isError || !account) {
		return (
			<Result
				status="404"
				title={t("account:message.notFound")}
				subTitle={t("account:message.notFoundDescription")}
				extra={<Link to="/account">{t("account:action.backToList")}</Link>}
			/>
		);
	}

	const canEdit = canModifyEntity("account", userRole);

	return (
		<div className="af-flex-column af-full-height">
			<ItemPageHeader
				icon={getArea("account")?.icon}
				title={account.name}
				details={[
					{
						label: t("account:label.tenant"),
						content: account.tenant?.name,
					},
					{
						label: t("account:label.owner"),
						content: account.owner?.name,
					},
					{
						label: t("account:label.mainContact"),
						content: account.mainContact?.name || "-",
					},
				]}
				actions={
					<Button onClick={() => setIsContactCreateOpen(true)}>
						{t("contact:action.newContact")}
					</Button>
				}
			/>

			<ItemPageLayout
				rightPanel={
					<RelatedPanel
						sections={[
							{
								key: "notes",
								label: t("account:label.notes"),
								children: <NotesList notes={[] as Note[]} />,
							},
							{
								key: "tasks",
								label: t("account:label.tasks"),
								children: <TasksList tasks={[] as Task[]} />,
							},
							{
								key: "activity",
								label: t("account:label.activity"),
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
									label: t("account:label.tabMain"),
									children: <AccountMainForm disabled={!isEditing} />,
								},
							]}
						/>
					</AfForm>
				</Card>
			</ItemPageLayout>

			<Modal
				open={isContactCreateOpen}
				title={t("common:action.createEntity", { entity: t("contact:label.entity") })}
				onCancel={() => setIsContactCreateOpen(false)}
				footer={null}
				destroyOnHidden
			>
				<ContactCreationForm
					account={{ id: account.id, name: account.name }}
					onSuccess={() => setIsContactCreateOpen(false)}
					onCancel={() => setIsContactCreateOpen(false)}
				/>
			</Modal>
		</div>
	);
}
