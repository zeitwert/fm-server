import { useTranslation } from "react-i18next";
import type { Building, BuildingElement } from "../../../../types";
import { getConditionColor } from "../../../../utils/evaluationUtils";
import { ReportHeader, ReportFooter } from "../components";

interface ElementsPageProps {
	building: Building;
	elements: BuildingElement[];
	sectionNumber: number;
}

interface ElementRowProps {
	element: BuildingElement;
}

/**
 * Builds the combined description text for an element.
 * - Main description (first line)
 * - "Zustand:" line only if conditionDescription has a value
 * - "Massnahmen:" line only if measureDescription has a value
 */
function buildDescription(
	element: BuildingElement,
	zustandLabel: string,
	massnahmenLabel: string
): React.ReactNode[] {
	const parts: React.ReactNode[] = [];

	// Main description always shown (if present)
	if (element.description) {
		parts.push(<span key="desc">{element.description}</span>);
	}

	// Zustand line only if conditionDescription has a value
	if (element.conditionDescription) {
		parts.push(
			<span key="cond">
				<strong>{zustandLabel}:</strong> {element.conditionDescription}
			</span>
		);
	}

	// Massnahmen line only if measureDescription has a value
	if (element.measureDescription) {
		parts.push(
			<span key="meas">
				<strong>{massnahmenLabel}:</strong> {element.measureDescription}
			</span>
		);
	}

	return parts;
}

/**
 * Element Row - Single row in the Datenerhebung table
 */
function ElementRow({ element }: ElementRowProps) {
	const { t } = useTranslation();

	const conditionColor = getConditionColor(element.condition);
	const descriptionParts = buildDescription(
		element,
		t("building:label.conditionLabel"),
		t("building:label.measuresLabel")
	);

	return (
		<tr className="element-row">
			<td className="element-name-cell">{element.buildingPart?.name || "-"}</td>
			<td className="element-description-cell">
				{descriptionParts.length > 0 ? (
					<div className="description-combined">
						{descriptionParts.map((part, i) => (
							<div key={i} className="description-line">
								{part}
							</div>
						))}
					</div>
				) : (
					"-"
				)}
			</td>
			<td className="element-weight-cell">{element.weight ?? "-"}</td>
			<td className="element-condition-cell">
				<span className="condition-value">{element.condition ?? "-"}</span>
				<span className="condition-square" style={{ backgroundColor: conditionColor }} />
			</td>
		</tr>
	);
}

/**
 * Elements Page - "Datenerhebung am Objekt"
 * Displays element data collection in a simple table format.
 * 4 columns: Bauteil | Beschreibung | Anteil | Zustand
 *
 * Note: May span multiple pages - CSS handles breaks between rows.
 */
export function ElementsPage({ building, elements, sectionNumber }: ElementsPageProps) {
	const { t } = useTranslation();

	// Filter to elements with weight > 0
	const validElements = elements.filter((e) => e.weight && e.weight > 0);

	// Get rating year for the title
	const ratingYear = building.currentRating?.ratingDate
		? new Date(building.currentRating.ratingDate).getFullYear()
		: new Date().getFullYear();

	return (
		<div className="report-page report-page-content elements-page page-break">
			<ReportHeader building={building} />

			<div className="report-page-body">
				<h2 className="data-section-header">
					{sectionNumber}. {t("building:report.dataCollection")} ({ratingYear})
				</h2>

				<table className="data-collection-table">
					<thead>
						<tr>
							<th className="col-bauteil">{t("building:label.element")}</th>
							<th className="col-beschreibung">{t("building:label.description")}</th>
							<th className="col-anteil">{t("building:label.weight")}</th>
							<th className="col-zustand">{t("building:report.condition")}</th>
						</tr>
					</thead>
					<tbody>
						{validElements.map((element) => (
							<ElementRow key={element.id} element={element} />
						))}
					</tbody>
				</table>
			</div>

			<ReportFooter pageNumber={4} />
		</div>
	);
}
