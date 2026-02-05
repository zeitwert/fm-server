import { Typography } from "antd";
import { useTranslation } from "react-i18next";
import { ReportPreviewFrame } from "@/common/components/report";
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
import { usePrintStyles } from "./hooks";

// CSS URLs for iframe injection (isolated from main app styles)
import reportBaseCssUrl from "@/styles/report.css?url";
import buildingEvalCssUrl from "./building-evaluation-report.css?url";

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
	const printStyles = usePrintStyles(building);

	const elements = building.currentRating?.elements || [];

	if (!building.currentRating) {
		return (
			<div style={{ padding: 24, textAlign: "center" }}>
				<Text type="secondary">{t("building:message.evaluationNoRating")}</Text>
			</div>
		);
	}

	return (
		<ReportPreviewFrame
			title={t("building:report.evaluationReport")}
			cssUrls={[reportBaseCssUrl, buildingEvalCssUrl]}
			printStyles={printStyles}
		>
			<div id="evaluation-report" className="evaluation-report">
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
				<RenovationTimelinePage
					building={building}
					elements={elements}
					projection={projection}
					sectionNumber={5}
				/>

				{/* Page: Charts */}
				<ChartsPage building={building} projection={projection} sectionNumber={6} />

				{/* Pages: Costs Table */}
				<CostsTablePage building={building} projection={projection} sectionNumber={7} />

				{/* Last Page: Summary / One-Pager */}
				<SummaryPage building={building} projection={projection} inflationRate={inflationRate} />
			</div>
		</ReportPreviewFrame>
	);
}
