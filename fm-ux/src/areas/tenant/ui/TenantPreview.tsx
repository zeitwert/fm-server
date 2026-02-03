import { useEffect, useState } from "react";
import { Button, Descriptions, Spin, Result, Space, Typography } from "antd";
import { EditOutlined } from "@ant-design/icons";
import { useNavigate } from "@tanstack/react-router";
import { useTranslation } from "react-i18next";
import { useTenantQuery } from "../queries";
import { getLogoUrl } from "../../../common/api/client";
import { getArea } from "@/app/config/AppConfig";

const { Text, Paragraph } = Typography;

interface TenantPreviewProps {
	id: string;
	onClose: () => void;
}

export function TenantPreview({ id, onClose }: TenantPreviewProps) {
	const { t } = useTranslation();
	const navigate = useNavigate();
	const [logoError, setLogoError] = useState(false);

	const { data: tenant, isLoading, isError } = useTenantQuery(id);

	useEffect(() => {
		setLogoError(false);
	}, [id]);

	const handleEdit = () => {
		onClose();
		navigate({ to: `/tenant/${id}` });
	};

	if (isLoading) {
		return (
			<div className="af-flex-center af-p-48">
				<Spin />
			</div>
		);
	}

	if (isError || !tenant) {
		return <Result status="error" title={t("tenant:message.notFound")} />;
	}

	const logoUrl = getLogoUrl("tenant", id);

	return (
		<div className="af-preview-container">
			<div className="af-preview-avatar">
				{!logoError ? (
					<img
						src={logoUrl}
						alt={tenant.name}
						className="af-preview-avatar-image"
						onError={() => setLogoError(true)}
					/>
				) : (
					<div className="af-preview-avatar-placeholder">{getArea("tenant")?.icon}</div>
				)}
			</div>

			<div className="af-preview-name">
				<Text className="af-preview-name-text">{tenant.name}</Text>
			</div>

			<Descriptions column={1} size="small">
				<Descriptions.Item label={t("tenant:label.tenantType")}>
					{tenant.tenantType?.name || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("tenant:label.owner")}>
					{tenant.owner?.name || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("tenant:label.inflationRate")}>
					{tenant.inflationRate != null ? `${tenant.inflationRate}%` : "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("tenant:label.discountRate")}>
					{tenant.discountRate != null ? `${tenant.discountRate}%` : "-"}
				</Descriptions.Item>
			</Descriptions>

			{tenant.description && (
				<div>
					<Text className="af-preview-description-label">{t("tenant:label.description")}</Text>
					<Paragraph
						className="af-preview-description-text"
						ellipsis={{ rows: 3, expandable: true }}
					>
						{tenant.description}
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
