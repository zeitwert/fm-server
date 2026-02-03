import { formatDateGerman } from "../../../../utils/evaluationUtils";

interface ReportFooterProps {
	pageNumber?: number;
	isCoverPage?: boolean;
}

/**
 * Report Footer Component
 *
 * Displays the footer section of each report page:
 * - Left: Current date in German format (DD.MM.YYYY)
 * - Center: Page number (hidden on cover page)
 * - Right: "powered by zeitwert" trademark
 */
export function ReportFooter({ pageNumber, isCoverPage = false }: ReportFooterProps) {
	const currentDate = formatDateGerman();

	return (
		<div className="report-footer">
			<div className="report-footer-left">{currentDate}</div>
			<div className="report-footer-center">{!isCoverPage && pageNumber != null && pageNumber}</div>
			<div className="report-footer-right">powered by zeitwert</div>
		</div>
	);
}
