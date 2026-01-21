import { Card, Spin, Result, Tabs } from "antd";
import { TeamOutlined } from "@ant-design/icons";
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
import { contactApi } from "../api";
import { contactFormSchema, type ContactFormInput } from "../schemas";
import { ContactMainForm } from "./forms/ContactMainForm";
import type { Contact } from "../types";
import { useSessionStore } from "../../../session/model/sessionStore";
import { ROLE_ADMIN, ROLE_APP_ADMIN, ROLE_SUPER_USER } from "../../../session/model/types";

interface ContactPageProps {
	contactId: string;
}

function canEditContact(role?: string): boolean {
	return role === ROLE_ADMIN || role === ROLE_APP_ADMIN || role === ROLE_SUPER_USER;
}

export function ContactPage({ contactId }: ContactPageProps) {
	const { t } = useTranslation("contact");
	const navigate = useNavigate();
	const { sessionInfo } = useSessionStore();
	const userRole = sessionInfo?.user?.role?.id;

	const {
		entity: contact,
		form,
		isLoading,
		isError,
		isEditing,
		isDirty,
		isStoring,
		handleEdit,
		handleCancel,
		handleStore,
	} = useEditableEntity<Contact, ContactFormInput>({
		id: contactId,
		queryKey: ["contact"],
		queryFn: (id) => contactApi.get(id),
		updateFn: contactApi.update,
		schema: contactFormSchema,
	});

	if (isLoading) {
		return (
			<div className="af-loading-inline">
				<Spin size="large" />
			</div>
		);
	}

	if (isError || !contact) {
		return (
			<Result
				status="404"
				title={t("notFound")}
				subTitle={t("notFoundDescription")}
				extra={<a onClick={() => navigate({ to: "/contact" })}>{t("backToList")}</a>}
			/>
		);
	}

	const canEdit = canEditContact(userRole);

	return (
		<div className="af-flex-column af-full-height">
			<ItemPageHeader
				icon={<TeamOutlined />}
				title={contact.caption || `${contact.firstName ?? ""} ${contact.lastName ?? ""}`.trim()}
				details={[
					{
						label: t("account"),
						content: contact.account?.caption,
						link: contact.account ? `/account/${contact.account.id}` : undefined,
					},
					{
						label: t("email"),
						content: contact.email,
					},
					{
						label: t("mobile"),
						content: contact.mobile,
					},
					{
						label: t("owner"),
						content: contact.owner?.name,
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
									children: <ContactMainForm disabled={!isEditing} />,
								},
							]}
						/>
					</AfForm>
				</Card>
			</ItemPageLayout>
		</div>
	);
}
