import { useTranslation } from "react-i18next";
import type { Building } from "../../../../types";

// Logo path - served from public folder
const COMUNAS_LOGO_URL = "/images/comunas-logo.png";

interface ReportHeaderProps {
	building: Building;
	isCoverPage?: boolean;
}

/**
 * Report Header Component
 *
 * Displays the header section of each report page:
 * - Left: Report title with building and account name (hidden on cover page)
 * - Right: Comunas logo (shown on all pages)
 */
export function ReportHeader({ building, isCoverPage = false }: ReportHeaderProps) {
	const { t } = useTranslation();

	const reportTitle = t("building:report.evaluationReport");
	const headerText = [reportTitle, building.name, building.account?.name]
		.filter(Boolean)
		.join(", ");

	return (
		<div className="report-header">
			<div className="report-header-left">
				{!isCoverPage && headerText}
			</div>
			<div className="report-header-right">
				<img
					src={COMUNAS_LOGO_URL}
					alt="Comunas"
					className="report-logo"
				/>
			</div>
		</div>
	);
}
