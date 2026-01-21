import { useState } from "react";
import { Button, Card, Modal, Spin, Result, Tabs } from "antd";
import { BankOutlined } from "@ant-design/icons";
import { useTranslation } from "react-i18next";
import { useNavigate } from "@tanstack/react-router";
import { useEditableEntity } from "../../../common/hooks/useEditableEntity";
import { ItemPageHeader, ItemPageLayout, EditControls } from "../../../common/components/items";
import { AfForm } from "../../../common/components/form";
import { RelatedPanel } from "../../../common/components/related";
import { NotesList } from "../../../common/components/related/NotesList";
import { TasksList } from "../../../common/components/related/TasksList";
import { ActivityTimeline } from "../../../common/components/related/ActivityTimeline";
import type { Note } from "../../../common/components/related/NotesList";
import type { Task } from "../../../common/components/related/TasksList";
import type { Activity } from "../../../common/components/related/ActivityTimeline";
import { accountApi } from "../api";
import { accountFormSchema, type AccountFormInput } from "../schemas";
import { AccountMainForm } from "./forms/AccountMainForm";
import { ContactCreationForm } from "../../contact/ui/forms/ContactCreationForm";
import type { Account } from "../types";
import { useSessionStore } from "../../../session/model/sessionStore";
import { ROLE_ADMIN, ROLE_APP_ADMIN, ROLE_SUPER_USER } from "../../../session/model/types";

interface AccountPageProps {
	accountId: string;
}

function canEditAccount(role?: string): boolean {
	return role === ROLE_ADMIN || role === ROLE_APP_ADMIN || role === ROLE_SUPER_USER;
}

export function AccountPage({ accountId }: AccountPageProps) {
	const { t } = useTranslation("account");
	const { t: tContact } = useTranslation("contact");
	const navigate = useNavigate();
	const { sessionInfo } = useSessionStore();
	const userRole = sessionInfo?.user?.role?.id;
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
	} = useEditableEntity<Account, AccountFormInput>({
		id: accountId,
		queryKey: ["account"],
		queryFn: (id) => accountApi.get(id),
		updateFn: accountApi.update,
		schema: accountFormSchema,
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
				title={t("notFound")}
				subTitle={t("notFoundDescription")}
				extra={<a onClick={() => navigate({ to: "/account" })}>{t("backToList")}</a>}
			/>
		);
	}

	const canEdit = canEditAccount(userRole);

	return (
		<div className="af-flex-column af-full-height">
			<ItemPageHeader
				icon={<BankOutlined />}
				title={account.name}
				details={[
					{
						label: t("tenant"),
						content: account.tenant?.name,
					},
					{
						label: t("owner"),
						content: account.owner?.name,
					},
					{
						label: t("mainContact"),
						content: account.mainContact?.name || "-",
					},
				]}
				actions={
					<Button onClick={() => setIsContactCreateOpen(true)}>
						{tContact("newContact")}
					</Button>
				}
			/>

			<ItemPageLayout
				rightPanel={
					<RelatedPanel
						sections={[
							{
								key: "notes",
								label: "Notizen",
								children: <NotesList notes={[] as Note[]} />,
							},
							{
								key: "tasks",
								label: "Aufgaben",
								children: <TasksList tasks={[] as Task[]} />,
							},
							{
								key: "activity",
								label: "Aktivität",
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
									label: t("tabMain"),
									children: <AccountMainForm disabled={!isEditing} />,
								},
							]}
						/>
					</AfForm>
				</Card>
			</ItemPageLayout>

			<Modal
				open={isContactCreateOpen}
				title={`${tContact("entitySingular")} erstellen`}
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
