import { Card, Spin, Result, Tabs } from "antd";
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
import { contactApi } from "../api";
import { contactKeys } from "../queries";
import { contactFormSchema, type ContactFormInput } from "../schemas";
import { ContactMainForm } from "./forms/ContactMainForm";
import type { Contact } from "../types";
import { useSessionStore } from "../../../session/model/sessionStore";
import { getArea } from "../../../app/config/AppConfig";

interface ContactPageProps {
	contactId: string;
}

export function ContactPage({ contactId }: ContactPageProps) {
	const { t } = useTranslation();
	const { sessionInfo } = useSessionStore();
	const userRole = sessionInfo?.user?.role?.id ?? "";

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
	} = useEntityQueries<Contact, ContactFormInput>({
		id: contactId,
		queryKey: contactKeys.details(),
		queryFn: (id) => contactApi.get(id),
		updateFn: contactApi.update,
		schema: contactFormSchema,
		listQueryKey: contactKeys.lists(),
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
				title={t("contact:message.notFound")}
				subTitle={t("contact:message.notFoundDescription")}
				extra={<Link to="/contact">{t("contact:action.backToList")}</Link>}
			/>
		);
	}

	const canEdit = canModifyEntity("contact", userRole);

	return (
		<div className="af-flex-column af-full-height">
			<ItemPageHeader
				icon={getArea("contact")?.icon}
				title={contact.caption || `${contact.firstName ?? ""} ${contact.lastName ?? ""}`.trim()}
				details={[
					{
						label: t("contact:label.account"),
						content: contact.account?.caption,
						link: contact.account ? `/account/${contact.account.id}` : undefined,
					},
					{
						label: t("contact:label.email"),
						content: contact.email,
					},
					{
						label: t("contact:label.mobile"),
						content: contact.mobile,
					},
					{
						label: t("contact:label.owner"),
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
								label: t("contact:label.notes"),
								children: <NotesList notes={[] as Note[]} />,
							},
							{
								key: "tasks",
								label: t("contact:label.tasks"),
								children: <TasksList tasks={[] as Task[]} />,
							},
							{
								key: "activity",
								label: t("contact:label.activity"),
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
									label: t("contact:label.tabMain"),
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
