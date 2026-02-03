import { useMemo, useState } from "react";
import { Checkbox, Col, InputNumber, Row, Table, Typography } from "antd";
import type { ColumnType } from "antd/es/table";
import { useTranslation } from "react-i18next";
import { AfSelect, AfDatePicker, AfFieldGroup } from "@/common/components/form";
import type { Building, BuildingElement } from "../../types";

const { Text } = Typography;

const SHORT_TERM_YEARS = 1;
const MID_TERM_YEARS = 5;

interface BuildingRatingFormProps {
	building: Building;
	disabled: boolean;
}

export function BuildingRatingForm({ building, disabled }: BuildingRatingFormProps) {
	const { t } = useTranslation();
	const [showAllElements, setShowAllElements] = useState(false);

	const currentRating = building.currentRating;
	const elements = useMemo(() => currentRating?.elements ?? [], [currentRating?.elements]);

	const filteredElements = useMemo(() => {
		if (showAllElements) return elements;
		return elements.filter((e) => e.weight && e.weight > 0);
	}, [elements, showAllElements]);

	const weightSum = useMemo(() => {
		return elements.reduce((sum, el) => sum + (el.weight ?? 0), 0);
	}, [elements]);

	const isWeightValid = weightSum === 100;

	const currentYear = new Date().getFullYear();

	const getRestorationCategory = (
		restorationYear: number | undefined
	): "short" | "mid" | "long" | null => {
		if (!restorationYear) return null;
		const yearsUntil = restorationYear - currentYear;
		if (yearsUntil <= SHORT_TERM_YEARS) return "short";
		if (yearsUntil <= MID_TERM_YEARS) return "mid";
		return "long";
	};

	const formatRestoration = (element: BuildingElement, category: "short" | "mid" | "long") => {
		const actualCategory = getRestorationCategory(element.restorationYear);
		if (actualCategory !== category) return null;
		const age = element.restorationYear ? element.restorationYear - currentYear : undefined;
		return element.restorationYear ? `${element.restorationYear} (${age})` : null;
	};

	const columns: ColumnType<BuildingElement>[] = [
		{
			title: t("building:label.element"),
			dataIndex: ["buildingPart", "name"],
			key: "buildingPart",
			width: 180,
		},
		{
			title: t("building:label.weight"),
			dataIndex: "weight",
			key: "weight",
			width: 80,
			align: "right",
			render: (value) => (
				<InputNumber
					value={value}
					min={0}
					max={100}
					size="small"
					style={{ width: 60 }}
					disabled={disabled}
				/>
			),
		},
		{
			title: t("building:label.condition"),
			dataIndex: "condition",
			key: "condition",
			width: 80,
			align: "right",
			render: (value, record) => (
				<InputNumber
					value={value}
					min={0}
					max={100}
					size="small"
					style={{ width: 60 }}
					disabled={disabled || !record.weight}
				/>
			),
		},
		{
			title: t("building:label.shortTermRestoration"),
			key: "shortTerm",
			width: 100,
			align: "center",
			render: (_, record) => formatRestoration(record, "short"),
		},
		{
			title: t("building:label.midTermRestoration"),
			key: "midTerm",
			width: 100,
			align: "center",
			render: (_, record) => formatRestoration(record, "mid"),
		},
		{
			title: t("building:label.longTermRestoration"),
			key: "longTerm",
			width: 100,
			align: "center",
			render: (_, record) => formatRestoration(record, "long"),
		},
		{
			title: t("building:label.restorationCosts"),
			dataIndex: "restorationCosts",
			key: "restorationCosts",
			width: 100,
			align: "right",
			render: (value) => (value ? `${value} kCHF` : null),
		},
		{
			title: t("building:label.descriptions"),
			key: "descriptions",
			render: (_, record) => {
				const parts = [];
				if (record.description) parts.push(record.description);
				if (record.conditionDescription)
					parts.push(`${t("building:label.conditionLabel")}: ${record.conditionDescription}`);
				if (record.measureDescription)
					parts.push(`${t("building:label.measuresLabel")}: ${record.measureDescription}`);
				return parts.join(" | ") || null;
			},
		},
	];

	if (!currentRating) {
		return (
			<div className="af-flex-center af-p-48">
				<Text type="secondary">{t("building:message.noRating")}</Text>
			</div>
		);
	}

	return (
		<div>
			<AfFieldGroup>
				<Row gutter={16}>
					<Col span={4}>
						<AfSelect
							name="currentRating.partCatalog"
							label={t("building:label.partCatalog")}
							source="building/codeBuildingPartCatalog"
							readOnly={disabled || !!currentRating.partCatalog}
							size={24}
						/>
					</Col>
					<Col span={2}>
						<div style={{ paddingTop: 28 }}>
							<Checkbox
								checked={showAllElements}
								onChange={(e) => setShowAllElements(e.target.checked)}
							>
								{t("building:label.showAllElements")}
							</Checkbox>
						</div>
					</Col>
					<Col span={4}>
						<AfSelect
							name="currentRating.maintenanceStrategy"
							label={t("building:label.maintenanceStrategy")}
							source="building/codeBuildingMaintenanceStrategy"
							readOnly
							size={24}
						/>
					</Col>
					<Col span={3}>
						<AfDatePicker
							name="currentRating.ratingDate"
							label={t("building:label.ratingDate")}
							readOnly={disabled}
							size={24}
						/>
					</Col>
					<Col span={4}>
						<AfSelect
							name="currentRating.ratingUser"
							label={t("building:label.ratingUser")}
							source="oe/objUser"
							readOnly={disabled}
							size={24}
						/>
					</Col>
					<Col span={1} />
					<Col span={4}>
						<AfSelect
							name="currentRating.ratingStatus"
							label={t("building:label.ratingStatus")}
							source="building/codeBuildingRatingStatus"
							readOnly
							size={24}
						/>
					</Col>
				</Row>
			</AfFieldGroup>

			{elements.length > 0 && (
				<div style={{ marginTop: 16 }}>
					<AfFieldGroup
						legend={`${t("building:label.ratingNumber")} #${(currentRating.seqNr ?? 0) + 1}${
							currentRating.ratingDate
								? ` (${t("building:label.asOf")} ${currentRating.ratingDate})`
								: ""
						}`}
					>
						<Row style={{ marginBottom: 8 }}>
							<Col span={6} />
							<Col span={6} style={{ textAlign: "center" }}>
								<Text strong>{t("building:label.restorationTimeframe")}</Text>
							</Col>
							<Col span={12} />
						</Row>
						<Row style={{ marginBottom: 8 }}>
							<Col span={6} />
							<Col span={2} style={{ textAlign: "center" }}>
								<Text type="secondary">0-1 {t("building:label.years")}</Text>
							</Col>
							<Col span={2} style={{ textAlign: "center" }}>
								<Text type="secondary">2-5 {t("building:label.years")}</Text>
							</Col>
							<Col span={2} style={{ textAlign: "center" }}>
								<Text type="secondary">&gt;5 {t("building:label.years")}</Text>
							</Col>
							<Col span={12} />
						</Row>

						<Table<BuildingElement>
							columns={columns}
							dataSource={filteredElements}
							rowKey="id"
							size="small"
							pagination={false}
							scroll={{ y: 400 }}
						/>

						<Row style={{ marginTop: 8 }}>
							<Col span={4}>
								<Text strong>{t("building:label.total")}</Text>
							</Col>
							<Col span={2} style={{ textAlign: "right" }}>
								<Text type={isWeightValid ? undefined : "danger"} strong>
									{weightSum}%
								</Text>
								{!isWeightValid && (
									<Text type="danger" style={{ marginLeft: 8 }}>
										({t("building:message.weightMustBe100")})
									</Text>
								)}
							</Col>
						</Row>
					</AfFieldGroup>
				</div>
			)}
		</div>
	);
}
