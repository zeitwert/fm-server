import { useEffect, useMemo, useCallback, useRef } from "react";
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
import { usePortfolioQuery, useUpdatePortfolio } from "../queries";
import { portfolioFormSchema, type PortfolioFormInput } from "../schemas";
import { PortfolioMainForm, type AvailableObject } from "./forms/PortfolioMainForm";
import type { Portfolio, PortfolioObject } from "../types";
import { useSessionStore } from "../../../session/model/sessionStore";
import { getArea } from "../../../app/config/AppConfig";
import { useQuery } from "@tanstack/react-query";
import { accountListApi } from "../../account/api";
import { portfolioListApi, buildingListApi } from "../api";

interface PortfolioPageProps {
	portfolioId: string;
}

export function PortfolioPage({ portfolioId }: PortfolioPageProps) {
	const { t } = useTranslation();
	const { sessionInfo } = useSessionStore();
	const userRole = sessionInfo?.user?.role?.id ?? "";

	const query = usePortfolioQuery(portfolioId);
	const updateMutation = useUpdatePortfolio();

	const { form, isEditing, isDirty, isStoring, handleEdit, handleCancel, handleStore } =
		usePersistentForm<Portfolio, PortfolioFormInput>({
			id: portfolioId,
			data: query.data,
			updateMutation,
			schema: portfolioFormSchema,
		});

	const portfolio = query.data;

	// Fetch available objects for dropdowns
	const { data: accounts = [] } = useQuery({
		queryKey: ["account", "list", "portfolio-dropdown"],
		queryFn: () => accountListApi.list(),
	});

	const { data: portfolios = [] } = useQuery({
		queryKey: ["portfolio", "list", "portfolio-dropdown"],
		queryFn: () => portfolioListApi.list(),
	});

	const { data: buildings = [] } = useQuery({
		queryKey: ["building", "list", "portfolio-dropdown"],
		queryFn: () => buildingListApi.list(),
	});

	const availableObjects: AvailableObject[] = useMemo(() => {
		const accountObjs = accounts.map((a) => ({
			id: a.id,
			name: `Kunde: ${a.name}`,
			itemType: { id: "obj_account", name: "Kunde" },
		}));

		const portfolioObjs = portfolios
			.filter((p) => p.id !== portfolioId) // Exclude current portfolio
			.map((p) => ({
				id: p.id,
				name: `Portfolio: ${p.name}`,
				itemType: { id: "obj_portfolio", name: "Portfolio" },
			}));

		const buildingObjs = buildings.map((b) => ({
			id: b.id,
			name: `Immobilie: ${b.caption || b.name}`,
			itemType: { id: "obj_building", name: "Immobilie" },
		}));

		return [...accountObjs, ...portfolioObjs, ...buildingObjs].sort((a, b) =>
			a.name.localeCompare(b.name)
		);
	}, [accounts, portfolios, buildings, portfolioId]);

	// Watch includes/excludes for calculation trigger
	const includes = form.watch("includes") ?? [];
	const excludes = form.watch("excludes") ?? [];

	// Track previous values to detect changes
	const prevIncludesRef = useRef<PortfolioObject[]>(includes);
	const prevExcludesRef = useRef<PortfolioObject[]>(excludes);

	// Trigger calculation when includes/excludes change during editing
	useEffect(() => {
		if (!isEditing || !portfolio) return;

		// Check if includes or excludes actually changed (compare by JSON to handle array reference changes)
		const includesChanged = JSON.stringify(includes) !== JSON.stringify(prevIncludesRef.current);
		const excludesChanged = JSON.stringify(excludes) !== JSON.stringify(prevExcludesRef.current);

		if (!includesChanged && !excludesChanged) return;

		// Update refs
		prevIncludesRef.current = includes;
		prevExcludesRef.current = excludes;

		// Trigger calculation
		const triggerCalculation = async () => {
			try {
				const result = await updateMutation.mutateAsync({
					id: portfolioId,
					includes,
					excludes,
					meta: {
						clientVersion: portfolio.meta?.version,
						operations: ["calculationOnly"],
					},
				});

				// Update buildings to reflect calculated state (without marking dirty)
				form.setValue("buildings", result.buildings, { shouldDirty: false });
			} catch (error) {
				console.error("Calculation failed:", error);
			}
		};

		triggerCalculation();
	}, [includes, excludes, isEditing, portfolio, portfolioId, updateMutation, form]);

	// Simplified handlers using form.setValue directly
	const handleAddInclude = useCallback(
		(obj: AvailableObject) => {
			const current = form.getValues("includes") ?? [];
			const newInclude: PortfolioObject = {
				id: obj.id,
				name: obj.name,
				itemType: obj.itemType,
			};
			form.setValue("includes", [...current, newInclude], { shouldDirty: true });
		},
		[form]
	);

	const handleRemoveInclude = useCallback(
		(id: string) => {
			const current = form.getValues("includes") ?? [];
			form.setValue(
				"includes",
				current.filter((i) => i.id !== id),
				{ shouldDirty: true }
			);
		},
		[form]
	);

	const handleAddExclude = useCallback(
		(obj: AvailableObject) => {
			const current = form.getValues("excludes") ?? [];
			const newExclude: PortfolioObject = {
				id: obj.id,
				name: obj.name,
				itemType: obj.itemType,
			};
			form.setValue("excludes", [...current, newExclude], { shouldDirty: true });
		},
		[form]
	);

	const handleRemoveExclude = useCallback(
		(id: string) => {
			const current = form.getValues("excludes") ?? [];
			form.setValue(
				"excludes",
				current.filter((e) => e.id !== id),
				{ shouldDirty: true }
			);
		},
		[form]
	);

	if (query.isLoading) {
		return (
			<div className="af-loading-inline">
				<Spin size="large" />
			</div>
		);
	}

	if (query.isError || !portfolio) {
		return (
			<Result
				status="404"
				title={t("portfolio:message.notFound")}
				subTitle={t("portfolio:message.notFoundDescription")}
				extra={<Link to="/portfolio">{t("portfolio:action.backToList")}</Link>}
			/>
		);
	}

	const canEdit = canModifyEntity("portfolio", userRole);

	return (
		<div className="af-flex-column af-full-height">
			<ItemPageHeader
				icon={getArea("portfolio")?.icon}
				title={portfolio.name}
				details={[
					{
						label: t("portfolio:label.tenant"),
						content: portfolio.tenant?.name,
					},
					{
						label: t("portfolio:label.owner"),
						content: portfolio.owner?.name,
					},
					{
						label: t("portfolio:label.account"),
						content: portfolio.account?.name || "-",
					},
				]}
			/>

			<ItemPageLayout
				rightPanel={
					<RelatedPanel
						sections={[
							{
								key: "notes",
								label: t("portfolio:label.notes"),
								children: <NotesList notes={[] as Note[]} />,
							},
							{
								key: "tasks",
								label: t("portfolio:label.tasks"),
								children: <TasksList tasks={[] as Task[]} />,
							},
							{
								key: "activity",
								label: t("portfolio:label.activity"),
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
									label: t("portfolio:label.tabMain"),
									children: (
										<PortfolioMainForm
											disabled={!isEditing}
											availableObjects={availableObjects}
											onAddInclude={handleAddInclude}
											onRemoveInclude={handleRemoveInclude}
											onAddExclude={handleAddExclude}
											onRemoveExclude={handleRemoveExclude}
										/>
									),
								},
							]}
						/>
					</AfForm>
				</Card>
			</ItemPageLayout>
		</div>
	);
}
