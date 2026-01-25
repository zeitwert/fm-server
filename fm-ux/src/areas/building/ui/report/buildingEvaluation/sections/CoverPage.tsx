import { useTranslation } from "react-i18next";
import type { Building } from "../../../../types";
import { COVER_PHOTO } from "../../../../utils/evaluationUtils";
import { ReportHeader, ReportFooter } from "../components";

interface CoverPageProps {
	building: Building;
	coverPhotoUrl?: string;
}

/**
 * Cover Page (Page 1)
 * Pixel-perfect layout matching the original Aspose Word template.
 * Uses absolute positioning for precise element placement.
 *
 * Cover photo dimensions from Aspose: 400×230pt (~141×81mm)
 */
export function CoverPage({ building, coverPhotoUrl }: CoverPageProps) {
	const { t } = useTranslation();

	return (
		<div className="report-page report-page-content cover-page">
			<ReportHeader building={building} isCoverPage />

			<div className="report-page-body cover-page-body">
				{/* Account name - top left */}
				{building.account?.name && (
					<p className="cover-account-name">{building.account.name}</p>
				)}

				{/* Building name - below account name */}
				<h1 className="cover-building-name">{building.name}</h1>

				{/* Photo container - right side */}
				<div
					className="cover-photo-container"
					style={{
						width: `${COVER_PHOTO.widthMm}mm`,
						height: `${COVER_PHOTO.heightMm}mm`,
					}}
				>
					{coverPhotoUrl ? (
						<img src={coverPhotoUrl} alt={building.name} className="cover-photo" />
					) : (
						<div className="cover-photo-placeholder">
							{t("building:report.noPhoto")}
						</div>
					)}
				</div>

				{/* Report subtitle - bottom left */}
				<p className="cover-strategic-label">
					{t("building:report.strategicMaintenance")}
				</p>

				{/* Report type - bottom left, below subtitle */}
				<p className="cover-report-type">{t("building:report.objectEvaluation")}</p>
			</div>

			<ReportFooter isCoverPage />
		</div>
	);
}
