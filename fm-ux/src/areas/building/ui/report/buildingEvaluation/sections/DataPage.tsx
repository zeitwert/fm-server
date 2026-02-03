import type { Building, ProjectionResult } from "../../../../types";
import {
	LOCATION_MAP,
	formatNumber,
	formatCHF,
	getShortTermCosts,
	getMidTermCosts,
	getLongTermCosts,
	getAverageMaintenanceCosts,
	calculateZNRatio,
} from "../../../../utils/evaluationUtils";
import { ReportHeader, ReportFooter } from "../components";

interface DataPageProps {
	building: Building;
	projection: ProjectionResult;
	locationMapUrl?: string;
	inflationRate?: number;
	startSectionNumber: number;
}

/**
 * Fact row for the FactsTable pattern
 * Only rows with non-null values are rendered
 */
interface FactRow {
	label: string;
	value: string;
}

/**
 * Renders a simple table of facts (label-value pairs)
 * Matching the original Aspose PDF layout without borders
 */
function FactsTable({ facts }: { facts: FactRow[] }) {
	return (
		<table className="data-table-simple">
			<tbody>
				{facts.map((fact, i) => (
					<tr key={i}>
						<th>{fact.label}</th>
						<td>{fact.value}</td>
					</tr>
				))}
			</tbody>
		</table>
	);
}

/**
 * Builds the address string from building fields
 * Format: "{street}, {zip} {city}, {country}"
 */
function buildAddress(building: Building): string | null {
	const parts: string[] = [];

	if (building.street) {
		parts.push(building.street);
	}

	const cityPart = [building.zip, building.city].filter(Boolean).join(" ");
	if (cityPart) {
		parts.push(cityPart);
	}

	if (building.country?.name) {
		parts.push(building.country.name);
	}

	return parts.length > 0 ? parts.join(", ") : null;
}

/**
 * Data Page (Page 2)
 * Displays basic building data table, evaluation parameters table, and location map.
 *
 * Layout: Two tables on the left (Grunddaten, Auswertung), location map on the right (Lage)
 * Uses numbered section headers matching original Aspose PDF.
 * Location map dimensions from Aspose: 360×360pt (~127×127mm)
 */
export function DataPage({
	building,
	projection,
	locationMapUrl,
	inflationRate = 2,
	startSectionNumber,
}: DataPageProps) {
	// Dynamic section counter
	let sectionNumber = startSectionNumber - 1;
	const nextSection = () => ++sectionNumber;

	// Calculate derived values
	const elements = building.currentRating?.elements || [];
	const znRatio = calculateZNRatio(elements);
	const periods = projection.periodList;
	const firstPeriod = periods[0];
	const timeValue = firstPeriod ? Math.round(firstPeriod.timeValue) : 0;

	// Build facts array - only include rows with values (FactsTable pattern from server)
	// Labels match server BuildingEvaluationServiceImpl.kt exactly
	const facts: FactRow[] = [
		building.buildingNr && {
			label: "Gebäudenummer",
			value: building.buildingNr,
		},
		building.currentRating?.partCatalog?.name && {
			label: "Gebäudekategorie",
			value: building.currentRating.partCatalog.name,
		},
		building.buildingYear &&
			building.buildingYear > 0 && {
				label: "Baujahr",
				value: String(building.buildingYear),
			},
		building.insuredValue && {
			// Dynamic label with year, matching server: "GV-Neuwert (" + building.insuredValueYear + ")"
			label: `GV-Neuwert (${building.insuredValueYear})`,
			value: formatCHF(building.insuredValue),
		},
		building.volume && {
			label: "Volumen Rauminhalt SIA 416",
			value: `${formatNumber(building.volume)} m³`,
		},
		building.currentRating?.ratingDate && {
			label: "Begehung am",
			value: building.currentRating.ratingDate,
		},
	].filter(Boolean) as FactRow[];

	// Build evaluation params array
	// Labels match server BuildingEvaluationServiceImpl.kt exactly
	const params: FactRow[] = [
		{
			label: "Laufzeit (Zeithorizont)",
			value: `${projection.duration} Jahre`,
		},
		{
			label: "Teuerung",
			value: `${inflationRate.toFixed(1)} %`,
		},
		{
			label: "Z/N Wert",
			value: String(znRatio),
		},
		{
			label: "Zeitwert",
			value: formatCHF(timeValue),
		},
		{
			label: "IS Kosten kurzfristig (0 - 1 Jahre)",
			value: formatCHF(getShortTermCosts(periods)),
		},
		{
			label: "IS Kosten mittelfristig (2 - 5 Jahre)",
			value: formatCHF(getMidTermCosts(periods)),
		},
		{
			label: "IS Kosten langfristig (6 - 25 Jahre)",
			value: formatCHF(getLongTermCosts(periods)),
		},
		{
			label: "Durchschnittliche IH Kosten (nächste 5 Jahre)",
			value: formatCHF(getAverageMaintenanceCosts(periods)),
		},
	];

	const address = buildAddress(building);

	return (
		<div className="report-page report-page-content">
			<ReportHeader building={building} />

			<div className="report-page-body data-page">
				<div className="data-tables">
					{/* Basic Data Table (Table 0 in Aspose) */}
					<div>
						<h2 className="data-section-header">{nextSection()}. Grunddaten</h2>
						<FactsTable facts={facts} />
					</div>

					{/* Evaluation Table (Table 1 in Aspose) */}
					<div>
						<h2 className="data-section-header">{nextSection()}. Auswertung</h2>
						<FactsTable facts={params} />
					</div>
				</div>

				{/* Location Section (Table 2 in Aspose) */}
				<div className="data-location-section">
					<h2 className="data-section-header">{nextSection()}. Lage</h2>
					<div
						className="location-map-container"
						style={{
							width: `${LOCATION_MAP.sizeMm}mm`,
							height: `${LOCATION_MAP.sizeMm}mm`,
						}}
					>
						{locationMapUrl ? (
							<img src={locationMapUrl} alt="Lageplan" className="location-map" />
						) : (
							<div
								style={{
									width: "100%",
									height: "100%",
									display: "flex",
									alignItems: "center",
									justifyContent: "center",
									color: "#999",
									fontSize: "12pt",
								}}
							>
								Keine Karte vorhanden
							</div>
						)}
					</div>
					{address && <div className="location-address">{address}</div>}
				</div>
			</div>

			<ReportFooter pageNumber={2} />
		</div>
	);
}
