import { useEffect, useState } from "react";
import { Button, Descriptions, Spin, Result, Space, Typography } from "antd";
import { EditOutlined } from "@ant-design/icons";
import { useNavigate } from "@tanstack/react-router";
import { useTranslation } from "react-i18next";
import { useBuildingQuery } from "../queries";
import { getArea } from "@/app/config/AppConfig";
import { getRestUrl } from "../../../common/api/client";

const { Text, Paragraph } = Typography;

interface BuildingPreviewProps {
	id: string;
	onClose: () => void;
}

export function BuildingPreview({ id, onClose }: BuildingPreviewProps) {
	const { t } = useTranslation();
	const navigate = useNavigate();
	const [logoError, setLogoError] = useState(false);

	const { data: building, isLoading, isError } = useBuildingQuery(id);

	useEffect(() => {
		setLogoError(false);
	}, [id]);

	const handleEdit = () => {
		onClose();
		navigate({ to: `/building/${id}` });
	};

	if (isLoading) {
		return (
			<div className="af-flex-center af-p-48">
				<Spin />
			</div>
		);
	}

	if (isError || !building) {
		return <Result status="error" title={t("building:message.notFound")} />;
	}

	// Buildings use coverFoto, not a dedicated logo endpoint
	const logoUrl = building.coverFoto?.id
		? getRestUrl("dms", `documents/${building.coverFoto.id}/content`)
		: undefined;

	return (
		<div className="af-preview-container">
			<div className="af-preview-avatar">
				{logoUrl && !logoError ? (
					<img
						src={logoUrl}
						alt={building.name}
						className="af-preview-avatar-image"
						onError={() => setLogoError(true)}
					/>
				) : (
					<div className="af-preview-avatar-placeholder">{getArea("building")?.icon}</div>
				)}
			</div>

			<div className="af-preview-name">
				<Text className="af-preview-name-text">{building.name}</Text>
			</div>

			<Descriptions column={1} size="small">
				<Descriptions.Item label={t("building:label.buildingNr")}>
					{building.buildingNr || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("building:label.owner")}>
					{building.owner?.name || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("building:label.address")}>
					{building.street && building.city
						? `${building.street}, ${building.zip ?? ""} ${building.city}`
						: "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("building:label.buildingType")}>
					{building.buildingType?.name || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("building:label.buildingYear")}>
					{building.buildingYear || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("building:label.insuredValue")}>
					{building.insuredValue ? `${building.insuredValue} kCHF` : "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("building:label.ratingStatus")}>
					{building.currentRating?.ratingStatus?.name || "-"}
				</Descriptions.Item>
			</Descriptions>

			{building.description && (
				<div>
					<Text className="af-preview-description-label">{t("building:label.description")}</Text>
					<Paragraph
						className="af-preview-description-text"
						ellipsis={{ rows: 3, expandable: true }}
					>
						{building.description}
					</Paragraph>
				</div>
			)}

			<Space className="af-preview-actions">
				<Button type="primary" icon={<EditOutlined />} onClick={handleEdit}>
					{t("common:action.edit")}
				</Button>
			</Space>
		</div>
	);
}
