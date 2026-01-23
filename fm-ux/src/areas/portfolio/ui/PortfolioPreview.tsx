import { Button, Descriptions, Spin, Result, Space, Typography } from "antd";
import { EditOutlined } from "@ant-design/icons";
import { useNavigate } from "@tanstack/react-router";
import { useTranslation } from "react-i18next";
import { usePortfolioQuery } from "../queries";
import { getArea } from "../../../app/config/AppConfig";

const { Text, Paragraph } = Typography;

interface PortfolioPreviewProps {
	id: string;
	onClose: () => void;
}

export function PortfolioPreview({ id, onClose }: PortfolioPreviewProps) {
	const { t } = useTranslation();
	const navigate = useNavigate();

	const { data: portfolio, isLoading, isError } = usePortfolioQuery(id);

	const handleEdit = () => {
		onClose();
		navigate({ to: `/portfolio/${id}` });
	};

	if (isLoading) {
		return (
			<div className="af-flex-center af-p-48">
				<Spin />
			</div>
		);
	}

	if (isError || !portfolio) {
		return <Result status="error" title={t("portfolio:message.notFound")} />;
	}

	return (
		<div className="af-preview-container">
			<div className="af-preview-avatar">
				<div className="af-preview-avatar-placeholder">{getArea("portfolio")?.icon}</div>
			</div>

			<div className="af-preview-name">
				<Text className="af-preview-name-text">{portfolio.name}</Text>
			</div>

			<Descriptions column={1} size="small">
				<Descriptions.Item label={t("portfolio:label.name")}>
					{portfolio.name || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("portfolio:label.portfolioNr")}>
					{portfolio.portfolioNr || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("portfolio:label.account")}>
					{portfolio.account?.name || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("portfolio:label.owner")}>
					{portfolio.owner?.name || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("portfolio:label.includes")}>
					{portfolio.includes?.length ?? 0}
				</Descriptions.Item>
				<Descriptions.Item label={t("portfolio:label.excludes")}>
					{portfolio.excludes?.length ?? 0}
				</Descriptions.Item>
				<Descriptions.Item label={t("portfolio:label.buildings")}>
					{portfolio.buildings?.length ?? 0}
				</Descriptions.Item>
			</Descriptions>

			{portfolio.description && (
				<div>
					<Text className="af-preview-description-label">{t("portfolio:label.description")}</Text>
					<Paragraph
						className="af-preview-description-text"
						ellipsis={{ rows: 3, expandable: true }}
					>
						{portfolio.description}
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
