import { useMemo, useState } from "react";
import { Button, Col, Row, Table, Typography } from "antd";
import type { ColumnType } from "antd/es/table";
import { CloseOutlined } from "@ant-design/icons";
import { useFormContext } from "react-hook-form";
import { useTranslation } from "react-i18next";
import {
	AfInput,
	AfTextArea,
	AfFieldRow,
	AfFieldGroup,
	AfItemSelect,
	type ItemSelectOption,
} from "../../../../common/components/form";
import type { PortfolioFormInput } from "../../schemas";
import type { PortfolioObject } from "../../types";

const { Text } = Typography;

// Re-export ItemSelectOption as AvailableObject for backward compatibility
export type AvailableObject = ItemSelectOption;

interface PortfolioMainFormProps {
	disabled: boolean;
	availableObjects: AvailableObject[];
	onAddInclude: (obj: AvailableObject) => void;
	onRemoveInclude: (id: string) => void;
	onAddExclude: (obj: AvailableObject) => void;
	onRemoveExclude: (id: string) => void;
}

export function PortfolioMainForm({
	disabled,
	availableObjects,
	onAddInclude,
	onRemoveInclude,
	onAddExclude,
	onRemoveExclude,
}: PortfolioMainFormProps) {
	const { t } = useTranslation();
	const { watch } = useFormContext<PortfolioFormInput>();

	const includes = watch("includes") ?? [];
	const excludes = watch("excludes") ?? [];
	const buildings = watch("buildings") ?? [];
	const account = watch("account");

	const includedIds = useMemo(() => new Set(includes.map((i) => i.id)), [includes]);
	const excludedIds = useMemo(() => new Set(excludes.map((e) => e.id)), [excludes]);

	const availableForInclude = useMemo(
		() => availableObjects.filter((obj) => !includedIds.has(obj.id) && !excludedIds.has(obj.id)),
		[availableObjects, includedIds, excludedIds]
	);

	const availableForExclude = useMemo(
		() => availableObjects.filter((obj) => !includedIds.has(obj.id) && !excludedIds.has(obj.id)),
		[availableObjects, includedIds, excludedIds]
	);

	const objectColumns: ColumnType<PortfolioObject>[] = [
		{
			title: t("portfolio:label.element"),
			dataIndex: "name",
			key: "name",
			width: "70%",
			render: (_, record) => {
				const typeId = record.itemType?.id?.substring(4);
				return typeId ? (
					<a href={`/${typeId}/${record.id}`}>{record.name}</a>
				) : (
					<Text>{record.name}</Text>
				);
			},
		},
		{
			title: t("portfolio:label.type"),
			dataIndex: ["itemType", "name"],
			key: "type",
			width: "25%",
		},
	];

	const includeColumns: ColumnType<PortfolioObject>[] = [
		...objectColumns,
		{
			title: t("portfolio:label.actionColumn"),
			key: "action",
			width: "5%",
			render: (_, record) =>
				!disabled && (
					<Button
						type="text"
						danger
						size="small"
						icon={<CloseOutlined />}
						onClick={() => onRemoveInclude(record.id)}
						title={t("portfolio:action.remove")}
					/>
				),
		},
	];

	const excludeColumns: ColumnType<PortfolioObject>[] = [
		...objectColumns,
		{
			title: t("portfolio:label.actionColumn"),
			key: "action",
			width: "5%",
			render: (_, record) =>
				!disabled && (
					<Button
						type="text"
						danger
						size="small"
						icon={<CloseOutlined />}
						onClick={() => onRemoveExclude(record.id)}
						title={t("portfolio:action.remove")}
					/>
				),
		},
	];

	const buildingColumns: ColumnType<PortfolioObject>[] = objectColumns;

	// State for controlled Select components (to clear after selection)
	const [includeSelectValue, setIncludeSelectValue] = useState<string | null>(null);
	const [excludeSelectValue, setExcludeSelectValue] = useState<string | null>(null);

	const handleIncludeSelect = (id: string) => {
		const obj = availableObjects.find((o) => o.id === id);
		if (obj) {
			onAddInclude(obj);
			setIncludeSelectValue(null);
		}
	};

	const handleExcludeSelect = (id: string) => {
		const obj = availableObjects.find((o) => o.id === id);
		if (obj) {
			onAddExclude(obj);
			setExcludeSelectValue(null);
		}
	};

	return (
		<div>
			<Row>
				<Col span={12}>
					<AfFieldGroup legend={t("portfolio:label.basicInfo")}>
						<AfFieldRow>
							<AfInput
								name="name"
								label={t("portfolio:label.name")}
								required
								readOnly={disabled}
								size={18}
							/>
							<AfInput
								name="portfolioNr"
								label={t("portfolio:label.portfolioNr")}
								readOnly={disabled}
								size={6}
							/>
						</AfFieldRow>
						<AfFieldRow>
							<div style={{ width: "100%" }}>
								<Typography.Text type="secondary" style={{ fontSize: 12 }}>
									{t("portfolio:label.account")}
								</Typography.Text>
								<Typography.Text style={{ display: "block", paddingTop: 4 }}>
									{account?.name ?? "-"}
								</Typography.Text>
							</div>
						</AfFieldRow>
					</AfFieldGroup>
				</Col>
				<Col span={12}>
					<AfFieldGroup legend="&nbsp;">
						<AfTextArea
							name="description"
							label={t("portfolio:label.description")}
							rows={4}
							readOnly={disabled}
							size={24}
						/>
					</AfFieldGroup>
				</Col>
			</Row>

			<Row style={{ marginTop: 16 }}>
				<Col span={12}>
					<AfFieldGroup legend={t("portfolio:label.includesCount", { count: includes.length })}>
						<div style={{ maxHeight: 200, overflowY: "auto" }}>
							<Table<PortfolioObject>
								columns={includeColumns}
								dataSource={includes}
								rowKey="id"
								size="small"
								pagination={false}
							/>
						</div>
					</AfFieldGroup>
				</Col>
				<Col span={12}>
					<AfFieldGroup legend={t("portfolio:label.excludesCount", { count: excludes.length })}>
						<div style={{ maxHeight: 200, overflowY: "auto" }}>
							<Table<PortfolioObject>
								columns={excludeColumns}
								dataSource={excludes}
								rowKey="id"
								size="small"
								pagination={false}
							/>
						</div>
					</AfFieldGroup>
				</Col>
			</Row>

			{!disabled && (
				<Row style={{ marginTop: 16 }}>
					<Col span={12}>
						<AfFieldGroup legend={t("portfolio:label.addInclude")}>
							<AfItemSelect
								value={includeSelectValue}
								onChange={handleIncludeSelect}
								options={availableForInclude}
								placeholder={t("portfolio:label.selectObject")}
								aria-label="portfolio:selectInclude"
							/>
						</AfFieldGroup>
					</Col>
					<Col span={12}>
						<AfFieldGroup legend={t("portfolio:label.addExclude")}>
							<AfItemSelect
								value={excludeSelectValue}
								onChange={handleExcludeSelect}
								options={availableForExclude}
								placeholder={t("portfolio:label.selectObject")}
								aria-label="portfolio:selectExclude"
							/>
						</AfFieldGroup>
					</Col>
				</Row>
			)}

			<Row style={{ marginTop: 16 }}>
				<Col span={24}>
					<AfFieldGroup legend={t("portfolio:label.buildingsCount", { count: buildings.length })}>
						<Table<PortfolioObject>
							columns={buildingColumns}
							dataSource={buildings}
							rowKey="id"
							size="small"
							pagination={false}
						/>
					</AfFieldGroup>
				</Col>
			</Row>
		</div>
	);
}
