import { Typography, Space, Empty, Spin, Card } from "antd";
import { useTranslation } from "react-i18next";
import type { Building } from "../../types";
import { useProjectionQuery } from "../../queries";
import { ProjectionValueChart } from "../components/ProjectionValueChart";
import { ProjectionCostChart } from "../components/ProjectionCostChart";
import { ProjectionTable } from "../components/ProjectionTable";
import { BuildingEvaluationReport } from "../report";
import { useContainerSize } from "@/common/hooks";
import { getRestUrl } from "@/common/api/client";

const { Text } = Typography;

export type EvaluationViewType = "chart" | "table" | "print";

interface BuildingEvaluationFormProps {
	building: Building;
	viewType: EvaluationViewType;
}

/**
 * Generate the location map URL for a building if coordinates are available.
 * Uses the server-side static map endpoint which returns a JPEG image.
 */
function getLocationMapUrl(building: Building): string | undefined {
	if (!building.geoCoordinates) return undefined;
	return `/rest/building/buildings/${building.id}/location`;
}

/**
 * Generate the cover photo URL for a building.
 * The server always provides an image, even if not explicitly defined.
 */
function getCoverPhotoUrl(building: Building): string {
	return getRestUrl("building", `buildings/${building.id}/coverFoto`);
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
					<ProjectionCostChart data={projection.periodList} elements={projection.elementList} />
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

	const renderPrintView = () => {
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

		// TODO: Get inflation rate from account settings
		return (
			<BuildingEvaluationReport
				building={building}
				projection={projection}
				coverPhotoUrl={getCoverPhotoUrl(building)}
				locationMapUrl={getLocationMapUrl(building)}
				inflationRate={2}
			/>
		);
	};

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
