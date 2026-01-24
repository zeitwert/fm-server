import { useState, useEffect, useMemo, useCallback } from "react";
import { Card, Spin, Result, Tabs } from "antd";
import { useTranslation } from "react-i18next";
import { Link } from "@tanstack/react-router";
import { useForm } from "react-hook-form";
import { standardSchemaResolver } from "@hookform/resolvers/standard-schema";
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
import { usePortfolioQuery, useUpdatePortfolio, portfolioKeys } from "../queries";
import { portfolioFormSchema, type PortfolioFormInput } from "../schemas";
import { PortfolioMainForm, type AvailableObject } from "./forms/PortfolioMainForm";
import type { PortfolioObject } from "../types";
import { useSessionStore } from "../../../session/model/sessionStore";
import { getArea } from "../../../app/config/AppConfig";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { accountListApi } from "../../account/api";
import { portfolioListApi, buildingListApi } from "../api";

interface PortfolioPageProps {
	portfolioId: string;
}

export function PortfolioPage({ portfolioId }: PortfolioPageProps) {
	const { t } = useTranslation();
	const { sessionInfo } = useSessionStore();
	const userRole = sessionInfo?.user?.role?.id ?? "";
	const queryClient = useQueryClient();

	const [isEditing, setIsEditing] = useState(false);
	const [localIncludes, setLocalIncludes] = useState<PortfolioObject[]>([]);
	const [localExcludes, setLocalExcludes] = useState<PortfolioObject[]>([]);
	const [includesExcludesDirty, setIncludesExcludesDirty] = useState(false);

	const query = usePortfolioQuery(portfolioId);
	const updateMutation = useUpdatePortfolio();

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

	const form = useForm<PortfolioFormInput>({
		resolver: standardSchemaResolver(portfolioFormSchema),
	});

	const portfolio = query.data;

	const isDirty = form.formState.isDirty || includesExcludesDirty;

	// Sync form with portfolio data (only when not editing to avoid resetting dirty state)
	useEffect(() => {
		if (portfolio && !isEditing) {
			form.reset({
				name: portfolio.name,
				portfolioNr: portfolio.portfolioNr,
				description: portfolio.description,
				account: portfolio.account,
				owner: portfolio.owner,
				includes: portfolio.includes,
				excludes: portfolio.excludes,
				buildings: portfolio.buildings,
			});
			setLocalIncludes(portfolio.includes ?? []);
			setLocalExcludes(portfolio.excludes ?? []);
			setIncludesExcludesDirty(false);
		}
	}, [portfolio, form, isEditing]);

	// Trigger calculation when includes/excludes change during edit
	const triggerCalculation = useCallback(
		async (newIncludes: PortfolioObject[], newExcludes: PortfolioObject[]) => {
			if (!portfolio) return;

			try {
				const result = await updateMutation.mutateAsync({
					id: portfolioId,
					includes: newIncludes,
					excludes: newExcludes,
					meta: {
						clientVersion: portfolio.meta?.version,
						operations: ["calculationOnly"],
					},
				});

				// Update form values to reflect calculated state
				form.setValue("buildings", result.buildings, { shouldDirty: false });
			} catch (error) {
				console.error("Calculation failed:", error);
			}
		},
		[portfolio, portfolioId, updateMutation, form]
	);

	const handleAddInclude = useCallback(
		(obj: AvailableObject) => {
			const newInclude: PortfolioObject = {
				id: obj.id,
				name: obj.name,
				itemType: obj.itemType,
			};
			const newIncludes = [...localIncludes, newInclude];
			setLocalIncludes(newIncludes);
			setIncludesExcludesDirty(true);
			form.setValue("includes", newIncludes, { shouldDirty: true });
			triggerCalculation(newIncludes, localExcludes);
		},
		[localIncludes, localExcludes, form, triggerCalculation]
	);

	const handleRemoveInclude = useCallback(
		(id: string) => {
			const newIncludes = localIncludes.filter((i) => i.id !== id);
			setLocalIncludes(newIncludes);
			setIncludesExcludesDirty(true);
			form.setValue("includes", newIncludes, { shouldDirty: true });
			triggerCalculation(newIncludes, localExcludes);
		},
		[localIncludes, localExcludes, form, triggerCalculation]
	);

	const handleAddExclude = useCallback(
		(obj: AvailableObject) => {
			const newExclude: PortfolioObject = {
				id: obj.id,
				name: obj.name,
				itemType: obj.itemType,
			};
			const newExcludes = [...localExcludes, newExclude];
			setLocalExcludes(newExcludes);
			setIncludesExcludesDirty(true);
			form.setValue("excludes", newExcludes, { shouldDirty: true });
			triggerCalculation(localIncludes, newExcludes);
		},
		[localIncludes, localExcludes, form, triggerCalculation]
	);

	const handleRemoveExclude = useCallback(
		(id: string) => {
			const newExcludes = localExcludes.filter((e) => e.id !== id);
			setLocalExcludes(newExcludes);
			setIncludesExcludesDirty(true);
			form.setValue("excludes", newExcludes, { shouldDirty: true });
			triggerCalculation(localIncludes, newExcludes);
		},
		[localIncludes, localExcludes, form, triggerCalculation]
	);

	const handleEdit = () => {
		setIsEditing(true);
	};

	const handleCancel = () => {
		if (portfolio) {
			form.reset({
				name: portfolio.name,
				portfolioNr: portfolio.portfolioNr,
				description: portfolio.description,
				account: portfolio.account,
				owner: portfolio.owner,
				includes: portfolio.includes,
				excludes: portfolio.excludes,
				buildings: portfolio.buildings,
			});
			setLocalIncludes(portfolio.includes ?? []);
			setLocalExcludes(portfolio.excludes ?? []);
		}
		setIncludesExcludesDirty(false);
		setIsEditing(false);
	};

	const handleStore = form.handleSubmit(async (formData) => {
		if (!portfolio) return;

		try {
			await updateMutation.mutateAsync({
				id: portfolioId,
				name: formData.name,
				portfolioNr: formData.portfolioNr ?? undefined,
				description: formData.description ?? undefined,
				owner: formData.owner!,
				includes: localIncludes,
				excludes: localExcludes,
				meta: { clientVersion: portfolio.meta?.version },
			});

			// Reset dirty flag and invalidate to get fresh data
			setIncludesExcludesDirty(false);
			queryClient.invalidateQueries({ queryKey: portfolioKeys.detail(portfolioId) });
			setIsEditing(false);
		} catch (error) {
			console.error("Save failed:", error);
		}
	});

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
									isStoring={updateMutation.isPending}
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
