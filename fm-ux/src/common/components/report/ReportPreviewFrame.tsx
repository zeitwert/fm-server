import { useCallback, useEffect, useRef, useState } from "react";
import { Button, InputNumber, Space, Tooltip, Typography } from "antd";
import {
	ColumnHeightOutlined,
	ColumnWidthOutlined,
	LeftOutlined,
	MinusOutlined,
	PlusOutlined,
	PrinterOutlined,
	RightOutlined,
} from "@ant-design/icons";
import { useTranslation } from "react-i18next";
import Frame from "react-frame-component";
import "./report-preview-frame.css";

const { Text } = Typography;

// A4 dimensions in mm
const A4_LANDSCAPE_WIDTH_MM = 297;
const A4_LANDSCAPE_HEIGHT_MM = 210;

// Conversion: 1mm ≈ 3.7795 pixels at 96dpi
const MM_TO_PX = 3.7795;

// Adobe-style preset zoom levels
const ZOOM_LEVELS = [25, 33, 50, 67, 75, 80, 90, 100, 110, 125, 150, 175, 200, 250, 300, 400, 500];
const MIN_ZOOM = ZOOM_LEVELS[0];
const MAX_ZOOM = ZOOM_LEVELS[ZOOM_LEVELS.length - 1];

export interface ReportPreviewFrameProps {
	/** Display title in toolbar */
	title: string;
	/** CSS files to inject into iframe */
	cssUrls: string[];
	/** Dynamic CSS to inject (e.g., @page rules with building data) */
	printStyles?: string;
	/** CSS selector for pages (default: ".report-page") */
	pageSelector?: string;
	/** Page width in mm (default: 297 for A4 landscape) */
	pageWidth?: number;
	/** Page height in mm (default: 210 for A4 landscape) */
	pageHeight?: number;
	/** Report content to render in iframe */
	children: React.ReactNode;
}

/**
 * ReportPreviewFrame - A reusable PDF viewer-like preview component
 *
 * Features:
 * - Page navigation (current/total, prev/next)
 * - Zoom controls (+/- and percentage input)
 * - Fit to width / fit to height toggle
 * - Print functionality
 */
export function ReportPreviewFrame({
	title,
	cssUrls,
	printStyles,
	pageSelector = ".report-page",
	pageWidth = A4_LANDSCAPE_WIDTH_MM,
	pageHeight = A4_LANDSCAPE_HEIGHT_MM,
	children,
}: ReportPreviewFrameProps) {
	const { t } = useTranslation();

	// Refs
	const iframeRef = useRef<HTMLIFrameElement>(null);
	const containerRef = useRef<HTMLDivElement>(null);
	const contentWrapperRef = useRef<HTMLDivElement>(null);

	// State
	// Note: We use "section" terminology in UI because preview shows logical
	// report sections, while actual printed pages may differ due to content flow
	const [currentSection, setCurrentSection] = useState(1);
	const [totalSections, setTotalSections] = useState(0);
	const [zoomLevel, setZoomLevel] = useState(100);
	const [fitMode, setFitMode] = useState<"width" | "height" | null>("width");
	const [isFrameReady, setIsFrameReady] = useState(false);

	// Page dimensions in pixels
	const pageWidthPx = pageWidth * MM_TO_PX;
	const pageHeightPx = pageHeight * MM_TO_PX;

	// Calculate fit-to-width zoom
	const calculateFitToWidth = useCallback(() => {
		const container = containerRef.current;
		if (!container) return 100;

		// Account for padding in the iframe body (16px on each side from report.css)
		const containerWidth = container.clientWidth;
		const zoom = ((containerWidth - 32) / pageWidthPx) * 100;
		return Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, Math.round(zoom)));
	}, [pageWidthPx]);

	// Calculate fit-to-height zoom
	const calculateFitToHeight = useCallback(() => {
		const container = containerRef.current;
		if (!container) return 100;

		// Account for padding in the iframe body (16px on each side from report.css)
		const containerHeight = container.clientHeight;
		const zoom = ((containerHeight - 32) / pageHeightPx) * 100;
		return Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, Math.round(zoom)));
	}, [pageHeightPx]);

	// Apply zoom to the iframe content wrapper
	const applyZoom = useCallback((zoom: number) => {
		const wrapper = contentWrapperRef.current;
		if (!wrapper) return;

		wrapper.style.transform = `scale(${zoom / 100})`;
		wrapper.style.transformOrigin = "top left";
		// Set width to maintain proper scroll behavior
		wrapper.style.width = `${100 / (zoom / 100)}%`;
	}, []);

	// Handle zoom change (from +/- buttons or manual input)
	const handleZoomChange = useCallback(
		(value: number | null) => {
			if (value !== null) {
				const clampedValue = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, value));
				setZoomLevel(clampedValue);
				setFitMode(null); // Clear fit mode when manually changing zoom
				applyZoom(clampedValue);
			}
		},
		[applyZoom]
	);

	// Zoom in/out handlers - snap to next/previous preset level
	const handleZoomIn = useCallback(() => {
		const nextLevel = ZOOM_LEVELS.find((level) => level > zoomLevel) ?? MAX_ZOOM;
		setZoomLevel(nextLevel);
		setFitMode(null); // Clear fit mode when manually changing zoom
		applyZoom(nextLevel);
	}, [zoomLevel, applyZoom]);

	const handleZoomOut = useCallback(() => {
		const prevLevel = [...ZOOM_LEVELS].reverse().find((level) => level < zoomLevel) ?? MIN_ZOOM;
		setZoomLevel(prevLevel);
		setFitMode(null); // Clear fit mode when manually changing zoom
		applyZoom(prevLevel);
	}, [zoomLevel, applyZoom]);

	// Fit to width handler
	const handleFitToWidth = useCallback(() => {
		const newZoom = calculateFitToWidth();
		setZoomLevel(newZoom);
		setFitMode("width");
		applyZoom(newZoom);
	}, [calculateFitToWidth, applyZoom]);

	// Fit to height handler
	const handleFitToHeight = useCallback(() => {
		const newZoom = calculateFitToHeight();
		setZoomLevel(newZoom);
		setFitMode("height");
		applyZoom(newZoom);
	}, [calculateFitToHeight, applyZoom]);

	// Navigate to specific section
	const navigateToSection = useCallback(
		(sectionNum: number) => {
			const iframe = iframeRef.current;
			if (!iframe?.contentDocument) return;

			const sections = iframe.contentDocument.querySelectorAll(pageSelector);
			const targetSection = sections[sectionNum - 1];
			if (targetSection) {
				targetSection.scrollIntoView({ behavior: "smooth", block: "start" });
			}
		},
		[pageSelector]
	);

	// Section navigation handlers
	const handlePrevSection = useCallback(() => {
		if (currentSection > 1) {
			const newSection = currentSection - 1;
			setCurrentSection(newSection);
			navigateToSection(newSection);
		}
	}, [currentSection, navigateToSection]);

	const handleNextSection = useCallback(() => {
		if (currentSection < totalSections) {
			const newSection = currentSection + 1;
			setCurrentSection(newSection);
			navigateToSection(newSection);
		}
	}, [currentSection, totalSections, navigateToSection]);

	const handleSectionInputChange = useCallback(
		(value: number | null) => {
			if (value !== null && value >= 1 && value <= totalSections) {
				setCurrentSection(value);
				navigateToSection(value);
			}
		},
		[totalSections, navigateToSection]
	);

	// Print handler
	const handlePrint = useCallback(() => {
		iframeRef.current?.contentWindow?.print();
	}, []);

	// Setup IntersectionObserver for section detection
	useEffect(() => {
		if (!isFrameReady) return;

		const iframe = iframeRef.current;
		if (!iframe?.contentDocument) return;

		const sections = iframe.contentDocument.querySelectorAll(pageSelector);
		setTotalSections(sections.length);

		if (sections.length === 0) return;

		// Create observer to track which section is most visible
		const observer = new IntersectionObserver(
			(entries) => {
				// Find the entry with highest intersection ratio
				let maxRatio = 0;
				let visibleSectionIndex = 0;

				entries.forEach((entry) => {
					if (entry.isIntersecting && entry.intersectionRatio > maxRatio) {
						maxRatio = entry.intersectionRatio;
						const index = Array.from(sections).indexOf(entry.target as Element);
						if (index !== -1) {
							visibleSectionIndex = index;
						}
					}
				});

				if (maxRatio > 0) {
					setCurrentSection(visibleSectionIndex + 1);
				}
			},
			{
				root: iframe.contentDocument,
				threshold: [0, 0.25, 0.5, 0.75, 1],
			}
		);

		sections.forEach((section) => observer.observe(section));

		return () => observer.disconnect();
	}, [isFrameReady, pageSelector]);

	// Apply initial fit-to-width on mount and resize
	useEffect(() => {
		if (!isFrameReady) return;

		// Initial fit to width
		handleFitToWidth();

		// Handle resize - reapply current fit mode if set
		const handleResize = () => {
			if (fitMode === "width") {
				handleFitToWidth();
			} else if (fitMode === "height") {
				handleFitToHeight();
			}
			// If fitMode is null (manual zoom), don't change zoom on resize
		};

		window.addEventListener("resize", handleResize);
		return () => window.removeEventListener("resize", handleResize);
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [isFrameReady]);

	// Frame content ready handler
	const handleFrameMount = useCallback(() => {
		// Small delay to ensure DOM is ready
		setTimeout(() => setIsFrameReady(true), 100);
	}, []);

	return (
		<div
			className="report-preview-frame"
			style={{ display: "flex", flexDirection: "column", height: "100%" }}
		>
			{/* Toolbar */}
			<div className="report-preview-toolbar">
				{/* Left: Title */}
				<Text strong className="report-preview-title">
					{title}
				</Text>

				{/* Center: Nav + Zoom + Fit buttons */}
				<div className="report-preview-controls-center">
					{/* Left: Section Navigation + Divider */}
					<div className="report-preview-controls-left">
						<Space.Compact size="small" className="report-preview-nav">
							<Button
								icon={<LeftOutlined />}
								onClick={handlePrevSection}
								disabled={currentSection <= 1}
								aria-label="common:previousSection"
							/>
							<InputNumber
								min={1}
								max={totalSections || 1}
								value={currentSection}
								onChange={handleSectionInputChange}
								controls={false}
								style={{ width: 45, textAlign: "center" }}
								aria-label="common:currentSection"
							/>
							<span className="report-preview-page-separator">/ {totalSections}</span>
							<Button
								icon={<RightOutlined />}
								onClick={handleNextSection}
								disabled={currentSection >= totalSections}
								aria-label="common:nextSection"
							/>
						</Space.Compact>
						<div className="report-preview-divider" />
					</div>

					{/* Center: Zoom Controls (always centered) */}
					<Space.Compact size="small" className="report-preview-zoom">
						<Button
							icon={<MinusOutlined />}
							onClick={handleZoomOut}
							disabled={zoomLevel <= MIN_ZOOM}
							aria-label="common:zoomOut"
						/>
						<InputNumber
							min={MIN_ZOOM}
							max={MAX_ZOOM}
							value={zoomLevel}
							onChange={handleZoomChange}
							formatter={(value) => `${value}%`}
							parser={(value) => parseInt(value?.replace("%", "") || "100", 10)}
							controls={false}
							style={{ width: 60, textAlign: "center" }}
							aria-label="common:zoomLevel"
						/>
						<Button
							icon={<PlusOutlined />}
							onClick={handleZoomIn}
							disabled={zoomLevel >= MAX_ZOOM}
							aria-label="common:zoomIn"
						/>
					</Space.Compact>

					{/* Right: Fit Buttons - show both except the active one */}
					<div className="report-preview-controls-right">
						{fitMode !== "width" && (
							<Tooltip title={t("common:fitToWidth")}>
								<Button
									icon={<ColumnWidthOutlined />}
									onClick={handleFitToWidth}
									size="small"
									aria-label="common:fitToWidth"
								/>
							</Tooltip>
						)}
						{fitMode !== "height" && (
							<Tooltip title={t("common:fitToHeight")}>
								<Button
									icon={<ColumnHeightOutlined />}
									onClick={handleFitToHeight}
									size="small"
									aria-label="common:fitToHeight"
								/>
							</Tooltip>
						)}
					</div>
				</div>

				{/* Right: Print Button */}
				<Button type="primary" icon={<PrinterOutlined />} onClick={handlePrint} size="small">
					{t("building:report.print")}
				</Button>
			</div>

			{/* Report Preview - Iframe */}
			<div ref={containerRef} className="report-preview-container">
				<Frame
					ref={iframeRef}
					style={{ border: "none", width: "100%", height: "100%", display: "block" }}
					head={
						<>
							{cssUrls.map((url) => (
								<link key={url} rel="stylesheet" href={url} />
							))}
							{printStyles && <style>{printStyles}</style>}
						</>
					}
					contentDidMount={handleFrameMount}
				>
					<div ref={contentWrapperRef} className="report-zoom-wrapper">
						{children}
					</div>
				</Frame>
			</div>
		</div>
	);
}
