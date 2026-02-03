import { useState, useCallback } from "react";
import { Button, Spin, Result, Tabs, Space, Modal, Segmented } from "antd";
import { LineChartOutlined, TableOutlined, PrinterOutlined } from "@ant-design/icons";
import { useTranslation } from "react-i18next";
import { Link } from "@tanstack/react-router";
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
import { usePersistentForm } from "../../../common/hooks";
import {
	useBuildingQuery,
	useUpdateBuilding,
	useAddBuildingRating,
	useMoveRatingStatus,
} from "../queries";
import { buildingFormSchema, type BuildingFormInput } from "../schemas";
import type { Building } from "../types";
import { BuildingMainForm } from "./forms/BuildingMainForm";
import { BuildingLocationForm } from "./forms/BuildingLocationForm";
import { BuildingRatingForm } from "./forms/BuildingRatingForm";
import { BuildingEvaluationForm, type EvaluationViewType } from "./forms/BuildingEvaluationForm";
import { useSessionStore } from "../../../session/model/sessionStore";

// Tab configuration: controls layout and edit behavior per tab
const TAB_CONFIG = {
	main: { fullWidth: false, showEditControls: true },
	location: { fullWidth: false, showEditControls: true },
	rating: { fullWidth: true, showEditControls: true },
	evaluation: { fullWidth: true, showEditControls: false },
} as const;

type TabKey = keyof typeof TAB_CONFIG;
import { getArea } from "../../../app/config/AppConfig";

interface BuildingPageProps {
	buildingId: string;
}

export function BuildingPage({ buildingId }: BuildingPageProps) {
	const { t } = useTranslation();
	const { sessionInfo } = useSessionStore();
	const userRole = sessionInfo?.user?.role?.id ?? "";

	const [activeTab, setActiveTab] = useState<TabKey>("main");
	const [evaluationViewType, setEvaluationViewType] = useState<EvaluationViewType>("chart");
	const [confirmAction, setConfirmAction] = useState<"deleteBuilding" | "discardRating" | null>(
		null
	);

	const query = useBuildingQuery(buildingId);
	const updateMutation = useUpdateBuilding();
	const addRatingMutation = useAddBuildingRating();
	const moveRatingMutation = useMoveRatingStatus();

	const { form, isEditing, isStoring, handleEdit, handleCancel, handleStore } = usePersistentForm<
		Building,
		BuildingFormInput
	>({
		id: buildingId,
		data: query.data,
		updateMutation,
		schema: buildingFormSchema,
	});

	const building = query.data;

	const ratingStatus = building?.currentRating?.ratingStatus?.id;
	const hasActiveRating = ratingStatus === "open" || ratingStatus === "review";
	const canEditRating = ratingStatus === "open";
	const canEditStaticData = ratingStatus !== "review";
	const validations = building?.meta?.validations;
	const hasErrors = Array.isArray(validations) && validations.length > 0;

	// Get tab configuration for current tab
	const currentTabConfig = TAB_CONFIG[activeTab];
	const isFullWidth = currentTabConfig.fullWidth;
	const showEditControls = currentTabConfig.showEditControls;

	// Determine if editing is allowed based on active tab
	const allowEdit =
		(canEditStaticData && (activeTab === "main" || activeTab === "location")) ||
		(canEditRating && activeTab === "rating");

	const handleAddRating = useCallback(async () => {
		if (!building) return;
		await addRatingMutation.mutateAsync({
			id: buildingId,
			clientVersion: building.meta?.version,
		});
	}, [building, buildingId, addRatingMutation]);

	const handleMoveRatingStatus = useCallback(
		async (status: string) => {
			if (!building) return;
			await moveRatingMutation.mutateAsync({
				id: buildingId,
				ratingStatusId: status,
				clientVersion: building.meta?.version,
			});
		},
		[building, buildingId, moveRatingMutation]
	);

	const handleDiscardRating = useCallback(async () => {
		setConfirmAction(null);
		await handleMoveRatingStatus("discard");
	}, [handleMoveRatingStatus]);

	const handleDeleteBuilding = useCallback(async () => {
		setConfirmAction(null);
		// TODO: Implement delete building API call
		// For now, navigate back to building list after delete
		console.log("Delete building:", buildingId);
	}, [buildingId]);

	if (query.isLoading) {
		return (
			<div className="af-loading-inline">
				<Spin size="large" />
			</div>
		);
	}

	if (query.isError || !building) {
		return (
			<Result
				status="404"
				title={t("building:message.notFound")}
				subTitle={t("building:message.notFoundDescription")}
				extra={<Link to="/building">{t("building:action.backToList")}</Link>}
			/>
		);
	}

	const canEdit = canModifyEntity("building", userRole);

	// Page-level workflow actions shown in the header (not form edit controls)
	const getHeaderActions = () => {
		// Don't show workflow actions while editing the form
		if (isEditing) return null;

		const actions: React.ReactNode[] = [];

		if (activeTab === "main" || activeTab === "location") {
			// Main/Location tab: Delete building action
			if (canEdit) {
				actions.push(
					<Button key="delete" danger onClick={() => setConfirmAction("deleteBuilding")}>
						{t("building:action.deleteBuilding")}
					</Button>
				);
			}
		} else if (activeTab === "rating") {
			// Rating tab: Rating workflow actions
			if (!ratingStatus) {
				actions.push(
					<Button key="add" type="primary" onClick={handleAddRating}>
						{t("building:action.newRating")}
					</Button>
				);
			} else if (ratingStatus === "open") {
				actions.push(
					<Button key="discard" danger onClick={() => setConfirmAction("discardRating")}>
						{t("building:action.discardRating")}
					</Button>,
					<Button key="review" type="primary" onClick={() => handleMoveRatingStatus("review")}>
						{t("building:action.submitForReview")}
					</Button>
				);
			} else if (ratingStatus === "review") {
				actions.push(
					<Button key="reject" onClick={() => handleMoveRatingStatus("open")}>
						{t("building:action.rejectRating")}
					</Button>,
					<Button key="accept" type="primary" onClick={() => handleMoveRatingStatus("done")}>
						{t("building:action.acceptRating")}
					</Button>
				);
			} else if (ratingStatus === "done") {
				actions.push(
					<Button key="new" onClick={handleAddRating}>
						{t("building:action.newRating")}
					</Button>,
					<Button key="reactivate" onClick={() => handleMoveRatingStatus("open")}>
						{t("building:action.reactivateRating")}
					</Button>
				);
			}
		}
		// Evaluation tab: No header actions (read-only projection view)

		return actions.length > 0 ? <Space>{actions}</Space> : null;
	};

	return (
		<div className="af-flex-column af-full-height">
			<ItemPageHeader
				icon={getArea("building")?.icon}
				title={building.name}
				details={[
					{
						label: t("building:label.tenant"),
						content: building.tenant?.name,
					},
					{
						label: t("building:label.owner"),
						content: building.owner?.name,
					},
					{
						label: t("building:label.address"),
						content:
							building.street && building.city
								? `${building.street}, ${building.zip ?? ""} ${building.city}`
								: undefined,
					},
				]}
				actions={getHeaderActions()}
			/>

			<ItemPageLayout
				fullWidth={isFullWidth}
				rightPanel={
					<RelatedPanel
						sections={[
							{
								key: "notes",
								label: t("building:label.notes"),
								children: <NotesList notes={[] as Note[]} />,
							},
							{
								key: "tasks",
								label: t("building:label.tasks"),
								children: <TasksList tasks={[] as Task[]} />,
							},
							{
								key: "activity",
								label: t("building:label.activity"),
								children: <ActivityTimeline activities={[] as Activity[]} />,
							},
						]}
					/>
				}
			>
				<AfForm form={form}>
					<Tabs
						activeKey={activeTab}
						onChange={(key) => setActiveTab(key as TabKey)}
						destroyInactiveTabPane
						tabBarExtraContent={
							showEditControls ? (
								<EditControls
									isEditing={isEditing}
									isDirty={form.formState.isDirty}
									isStoring={isStoring}
									canEdit={canEdit && allowEdit}
									onEdit={handleEdit}
									onCancel={handleCancel}
									onStore={handleStore}
								/>
							) : activeTab === "evaluation" ? (
								<Segmented
									value={evaluationViewType}
									onChange={(value) => setEvaluationViewType(value as EvaluationViewType)}
									options={[
										{
											value: "chart",
											icon: <LineChartOutlined />,
											label: t("building:label.viewChart"),
										},
										{
											value: "table",
											icon: <TableOutlined />,
											label: t("building:label.viewTable"),
										},
										{
											value: "print",
											icon: <PrinterOutlined />,
											label: t("building:label.viewPrint"),
										},
									]}
								/>
							) : null
						}
						items={[
							{
								key: "main",
								label: t("building:label.tabMain"),
								children: <BuildingMainForm disabled={!isEditing} />,
							},
							{
								key: "location",
								label: t("building:label.tabLocation"),
								children: <BuildingLocationForm disabled={!isEditing} />,
							},
							{
								key: "rating",
								label: (
									<span>
										{t("building:label.tabRating")}
										{hasActiveRating && <span style={{ color: "#014486" }}> *</span>}
									</span>
								),
								children: (
									<BuildingRatingForm building={building} disabled={!isEditing || !canEditRating} />
								),
							},
							{
								key: "evaluation",
								label: t("building:label.tabEvaluation"),
								disabled: isEditing || hasErrors,
								children: (
									<BuildingEvaluationForm building={building} viewType={evaluationViewType} />
								),
							},
						]}
					/>
				</AfForm>
			</ItemPageLayout>

			<Modal
				open={confirmAction !== null}
				title={
					confirmAction === "deleteBuilding"
						? t("building:action.deleteBuilding")
						: t("building:action.discardRating")
				}
				onCancel={() => setConfirmAction(null)}
				onOk={confirmAction === "deleteBuilding" ? handleDeleteBuilding : handleDiscardRating}
				okText={t("common:action.confirm")}
				cancelText={t("common:action.cancel")}
				okButtonProps={{ danger: true }}
			>
				<p>
					{confirmAction === "deleteBuilding"
						? t("building:message.deleteBuildingConfirm")
						: t("building:message.discardRatingConfirm")}
				</p>
			</Modal>
		</div>
	);
}
