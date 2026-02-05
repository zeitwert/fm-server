import { useMemo } from "react";
import { useTranslation } from "react-i18next";
import type { Building } from "../../../../types";
import { formatDateGerman } from "../../../../utils/evaluationUtils";

/**
 * Hook that generates dynamic @page CSS with building data for print output.
 *
 * Uses CSS @page margin boxes (Chrome 131+, Safari 18.2+) to add custom
 * headers/footers that appear on every printed page, replacing browser defaults.
 *
 * @param building - The building to include in the header text
 * @returns CSS string to inject into the iframe for print styling
 */
export function usePrintStyles(building: Building): string {
	const { t } = useTranslation();

	return useMemo(() => {
		const reportTitle = t("building:report.evaluationReport");
		const headerText = [reportTitle, building.name, building.account?.name]
			.filter(Boolean)
			.join(", ");
		const currentDate = formatDateGerman();

		// Escape quotes in dynamic content for CSS string safety
		const safeHeaderText = headerText.replace(/"/g, '\\"');

		// Font family must be explicitly set on @page margin boxes as they don't inherit from document
		const fontFamily = "'Gotham Narrow SSM', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif";

		return `
			@page {
				size: A4 landscape;
				/* Margins: 22mm top (15mm + 7mm for header), 20mm right/bottom/left */
				margin: 22mm 20mm 20mm 20mm;
				
				@top-left {
					content: "${safeHeaderText}";
					font-family: ${fontFamily};
					font-size: 9pt;
					color: #333;
					padding-top: 10mm;
				}
				
				@bottom-left {
					content: "${currentDate}";
					font-family: ${fontFamily};
					font-size: 8pt;
					color: #666;
					padding-bottom: 10mm;
				}
				
				@bottom-center {
					content: counter(page);
					font-family: ${fontFamily};
					font-size: 8pt;
					color: #666;
					padding-bottom: 10mm;
				}
				
				@bottom-right {
					content: "powered by zeitwert";
					font-family: ${fontFamily};
					font-size: 8pt;
					color: #666;
					padding-bottom: 10mm;
				}
			}
			
			/* Cover page - no header text, keep only page number */
			@page cover {
				@top-left {
					content: none;
				}
			}
		`;
	}, [building, t]);
}
