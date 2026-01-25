import { useRef, useCallback } from "react";
import { Button, Space, Typography } from "antd";
import { PrinterOutlined } from "@ant-design/icons";
import { useTranslation } from "react-i18next";
import type { Building, ProjectionResult } from "../../../types";
import {
	CoverPage,
	DataPage,
	ElementsPage,
	RenovationTimelinePage,
	ChartsPage,
	CostsTablePage,
	SummaryPage,
} from "./sections";
import "./report.css";

const { Text } = Typography;

interface BuildingEvaluationReportProps {
	building: Building;
	projection: ProjectionResult;
	coverPhotoUrl?: string;
	locationMapUrl?: string;
	inflationRate?: number;
}

/**
 * Building Evaluation Report - Main Orchestrator
 *
 * Assembles all report pages in the correct order for print output.
 * Based on the original Aspose Word template structure.
 *
 * Page Order:
 * 1. Cover Page - Building name, address, cover photo
 * 2. Data Page - Basic data + evaluation params + location map
 * 3+ Elements Page - Element ratings (may span multiple pages)
 * 4. Renovation Timeline - 25-year optimal renovation matrix
 * 5. Charts Page - Value chart + Cost chart
 * 6+ Costs Table - 25-year cost breakdown (may span multiple pages)
 * 7. Summary Page - One-pager overview
 */
export function BuildingEvaluationReport({
	building,
	projection,
	coverPhotoUrl,
	locationMapUrl,
	inflationRate = 2,
}: BuildingEvaluationReportProps) {
	const { t } = useTranslation();
	const reportRef = useRef<HTMLDivElement>(null);

	const elements = building.currentRating?.elements || [];

	// Handle print
	const handlePrint = useCallback(() => {
		window.print();
	}, []);

	// Chart image for SummaryPage - for now left undefined, would be generated from Recharts SVG in full implementation
	const costChartImage: string | undefined = undefined;

	if (!building.currentRating) {
		return (
			<div style={{ padding: 24, textAlign: "center" }}>
				<Text type="secondary">{t("building:message.evaluationNoRating")}</Text>
			</div>
		);
	}

	return (
		<div style={{ display: "flex", flexDirection: "column", height: "100%" }}>
			{/* Toolbar - hidden when printing */}
			<div
				className="no-print"
				style={{
					padding: "8px 16px",
					borderBottom: "1px solid #e0e0e0",
					background: "#fafafa",
					display: "flex",
					justifyContent: "space-between",
					alignItems: "center",
				}}
			>
				<Text strong>{t("building:report.evaluationReport")}</Text>
				<Space>
					<Button type="primary" icon={<PrinterOutlined />} onClick={handlePrint}>
						{t("building:report.print")}
					</Button>
				</Space>
			</div>

			{/* Report Preview */}
			<div
				style={{
					flex: 1,
					overflow: "auto",
					padding: 16,
					background: "#e8e8e8",
				}}
			>
				<div ref={reportRef} className="evaluation-report">
					{/* Page 1: Cover */}
					<CoverPage building={building} coverPhotoUrl={coverPhotoUrl} />

					{/* Page 2: Data */}
					<DataPage
						building={building}
						projection={projection}
						locationMapUrl={locationMapUrl}
						inflationRate={inflationRate}
						startSectionNumber={1}
					/>

					{/* Page 3+: Elements (Datenerhebung) */}
					<ElementsPage building={building} elements={elements} sectionNumber={4} />

					{/* Page: Renovation Timeline */}
					<RenovationTimelinePage building={building} elements={elements} projection={projection} sectionNumber={5} />

				{/* Page: Charts */}
				<ChartsPage building={building} projection={projection} sectionNumber={6} />

					{/* Pages: Costs Table */}
					<CostsTablePage building={building} projection={projection} sectionNumber={7} />

					{/* Last Page: Summary / One-Pager */}
					<SummaryPage
						building={building}
						projection={projection}
						inflationRate={inflationRate}
						costChartImage={costChartImage}
					/>
				</div>
			</div>
		</div>
	);
}
