import { Card, Spin, Result, Tabs } from "antd";
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
import type { Account } from "../types";
import { useSessionStore } from "../../../session/model/sessionStore";
import { ROLE_ADMIN, ROLE_APP_ADMIN, ROLE_SUPER_USER } from "../../../session/model/types";
import type { Enumerated } from "../../../common/types";

interface AccountPageProps {
	accountId: string;
}

function canEditAccount(role?: string): boolean {
	return role === ROLE_ADMIN || role === ROLE_APP_ADMIN || role === ROLE_SUPER_USER;
}

function transformToForm(account: Account): AccountFormInput {
	return {
		name: account.name,
		description: account.description ?? "",
		accountType: account.accountType,
		clientSegment: account.clientSegment ?? null,
		tenant: account.tenant,
		owner: account.owner,
		inflationRate: account.inflationRate ?? null,
		discountRate: account.discountRate ?? null,
	};
}

function transformFromForm(formData: Partial<AccountFormInput>): Partial<Account> {
	const result: Partial<Account> = {};

	if (formData.name !== undefined) result.name = formData.name;
	if (formData.description !== undefined) result.description = formData.description || undefined;
	if (formData.accountType !== undefined) result.accountType = formData.accountType as Enumerated;
	if (formData.clientSegment !== undefined)
		result.clientSegment = formData.clientSegment ?? undefined;
	if (formData.tenant !== undefined) result.tenant = formData.tenant as Enumerated;
	if (formData.owner !== undefined) result.owner = formData.owner as Enumerated;
	if (formData.inflationRate !== undefined)
		result.inflationRate = formData.inflationRate ?? undefined;
	if (formData.discountRate !== undefined) result.discountRate = formData.discountRate ?? undefined;

	return result;
}

export function AccountPage({ accountId }: AccountPageProps) {
	const { t } = useTranslation("account");
	const navigate = useNavigate();
	const { sessionInfo } = useSessionStore();
	const userRole = sessionInfo?.user?.role?.id;

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
		transformToForm,
		transformFromForm,
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
		<>
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
				<Card>
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
									children: <AccountMainForm disabled={!isEditing} contacts={account.contacts} />,
								},
							]}
						/>
					</AfForm>
				</Card>
			</ItemPageLayout>
		</>
	);
}
