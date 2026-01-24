import { Card, Typography, Space, Empty, Spin } from "antd";
import { PrinterOutlined } from "@ant-design/icons";
import { useTranslation } from "react-i18next";
import type { Building } from "../../types";
import { useProjectionQuery } from "../../queries";
import { ProjectionValueChart } from "../components/ProjectionValueChart";
import { ProjectionCostChart } from "../components/ProjectionCostChart";
import { ProjectionTable } from "../components/ProjectionTable";
import { useContainerSize } from "../../../../common/hooks";

const { Text, Title } = Typography;

export type EvaluationViewType = "chart" | "table" | "print";

interface BuildingEvaluationFormProps {
	building: Building;
	viewType: EvaluationViewType;
}

export function BuildingEvaluationForm({ building, viewType }: BuildingEvaluationFormProps) {
	const { t } = useTranslation();
	const [containerRef, containerSize] = useContainerSize<HTMLDivElement>();

	const validations = building.meta?.validations;
	const hasErrors = Array.isArray(validations) && validations.length > 0;
	const hasRating = !!building.currentRating;

	// Fetch projection data when rating is complete
	const { data: projection, isLoading, error } = useProjectionQuery(hasRating ? building.id : "");

	// Calculate chart heights from measured container size (30%/70% split with minimums)
	const valueChartHeight = Math.max(150, Math.floor(containerSize.height * 0.3));

	if (hasErrors) {
		return (
			<Card>
				<Empty
					image={Empty.PRESENTED_IMAGE_SIMPLE}
					description={
						<Space direction="vertical">
							<Text type="danger">{t("building:message.evaluationHasErrors")}</Text>
							<Text type="secondary">{t("building:message.fixValidationErrors")}</Text>
						</Space>
					}
				/>
			</Card>
		);
	}

	if (!hasRating) {
		return (
			<Card>
				<Empty
					image={Empty.PRESENTED_IMAGE_SIMPLE}
					description={
						<Space direction="vertical">
							<Text type="secondary">{t("building:message.evaluationNoRating")}</Text>
							<Text type="secondary">{t("building:message.completeRatingFirst")}</Text>
						</Space>
					}
				/>
			</Card>
		);
	}

	const renderChartView = () => {
		if (isLoading) {
			return (
				<div
					style={{
						flex: 1,
						display: "flex",
						justifyContent: "center",
						alignItems: "center",
					}}
				>
					<Spin size="large" />
				</div>
			);
		}

		if (error || !projection) {
			return (
				<Empty
					image={Empty.PRESENTED_IMAGE_SIMPLE}
					description={
						<Space direction="vertical">
							<Text type="danger">{t("building:message.projectionLoadError")}</Text>
						</Space>
					}
				/>
			);
		}

		if (projection.periodList.length === 0) {
			return (
				<Empty
					image={Empty.PRESENTED_IMAGE_SIMPLE}
					description={<Text type="secondary">{t("building:message.noProjectionData")}</Text>}
				/>
			);
		}

		return (
			<>
				{/* Value Chart - 30% height */}
				<div style={{ height: valueChartHeight, flexShrink: 0 }}>
					<ProjectionValueChart data={projection.periodList} containerWidth={containerSize.width} />
				</div>
				{/* Cost Chart - remaining height */}
				<div style={{ flex: 1, minHeight: 200 }}>
					<ProjectionCostChart
						data={projection.periodList}
						elements={projection.elementList}
					/>
				</div>
			</>
		);
	};

	const renderTableView = () => {
		if (isLoading) {
			return (
				<div
					style={{
						flex: 1,
						display: "flex",
						justifyContent: "center",
						alignItems: "center",
					}}
				>
					<Spin size="large" />
				</div>
			);
		}

		if (error || !projection) {
			return (
				<Empty
					image={Empty.PRESENTED_IMAGE_SIMPLE}
					description={
						<Space direction="vertical">
							<Text type="danger">{t("building:message.projectionLoadError")}</Text>
						</Space>
					}
				/>
			);
		}

		if (projection.periodList.length === 0) {
			return (
				<Empty
					image={Empty.PRESENTED_IMAGE_SIMPLE}
					description={<Text type="secondary">{t("building:message.noProjectionData")}</Text>}
				/>
			);
		}

		return <ProjectionTable periodList={projection.periodList} />;
	};

	const renderPrintView = () => (
		<div
			style={{
				flex: 1,
				display: "flex",
				flexDirection: "column",
				alignItems: "center",
				justifyContent: "center",
				backgroundColor: "#fafafa",
				borderRadius: 4,
				padding: 48,
			}}
		>
			<PrinterOutlined style={{ fontSize: 64, color: "#1890ff", marginBottom: 24 }} />
			<Title level={4}>{t("building:label.projectionPrint")}</Title>
			<Text type="secondary">{t("building:message.evaluationPlaceholder")}</Text>
		</div>
	);

	return (
		<div
			ref={containerRef}
			style={{ display: "flex", flexDirection: "column", height: "100%", minHeight: 0 }}
		>
			{viewType === "chart" && renderChartView()}
			{viewType === "table" && renderTableView()}
			{viewType === "print" && renderPrintView()}
		</div>
	);
}
