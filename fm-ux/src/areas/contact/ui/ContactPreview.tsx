import { Button, Descriptions, Spin, Result, Space, Typography } from "antd";
import { EditOutlined } from "@ant-design/icons";
import { useNavigate } from "@tanstack/react-router";
import { useTranslation } from "react-i18next";
import { useContactQuery } from "../queries";
import { getArea } from "@/app/config/AppConfig";

const { Text, Paragraph } = Typography;

interface ContactPreviewProps {
	id: string;
	onClose: () => void;
}

export function ContactPreview({ id, onClose }: ContactPreviewProps) {
	const { t } = useTranslation();
	const navigate = useNavigate();

	const { data: contact, isLoading, isError } = useContactQuery(id);

	const handleEdit = () => {
		onClose();
		navigate({ to: `/contact/${id}` });
	};

	if (isLoading) {
		return (
			<div className="af-flex-center af-p-48">
				<Spin />
			</div>
		);
	}

	if (isError || !contact) {
		return <Result status="error" title={t("contact:message.notFound")} />;
	}

	const displayName =
		contact.caption || `${contact.firstName ?? ""} ${contact.lastName ?? ""}`.trim();

	return (
		<div className="af-preview-container">
			<div className="af-preview-avatar">
				<div className="af-preview-avatar-placeholder">{getArea("contact")?.icon}</div>
			</div>

			<div className="af-preview-name">
				<Text className="af-preview-name-text">{displayName}</Text>
			</div>

			<Descriptions column={1} size="small">
				<Descriptions.Item label={t("contact:label.account")}>
					{contact.account?.caption || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("contact:label.email")}>
					{contact.email || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("contact:label.mobile")}>
					{contact.mobile || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("contact:label.phone")}>
					{contact.phone || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("contact:label.owner")}>
					{contact.owner?.name || "-"}
				</Descriptions.Item>
			</Descriptions>

			{contact.description && (
				<div>
					<Text className="af-preview-description-label">{t("contact:label.description")}</Text>
					<Paragraph
						className="af-preview-description-text"
						ellipsis={{ rows: 3, expandable: true }}
					>
						{contact.description}
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
