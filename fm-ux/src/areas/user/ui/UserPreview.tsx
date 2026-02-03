import { useEffect, useState } from "react";
import { Button, Descriptions, Spin, Result, Space, Typography } from "antd";
import { EditOutlined } from "@ant-design/icons";
import { useNavigate } from "@tanstack/react-router";
import { useTranslation } from "react-i18next";
import { useUserQuery } from "../queries";
import { getArea } from "@/app/config/AppConfig";

const { Text, Paragraph } = Typography;

interface UserPreviewProps {
	id: string;
	onClose: () => void;
}

export function UserPreview({ id, onClose }: UserPreviewProps) {
	const { t } = useTranslation();
	const navigate = useNavigate();
	const [avatarError, setAvatarError] = useState(false);

	const { data: user, isLoading, isError } = useUserQuery(id);

	useEffect(() => {
		setAvatarError(false);
	}, [id]);

	const handleEdit = () => {
		onClose();
		navigate({ to: `/user/${id}` });
	};

	if (isLoading) {
		return (
			<div className="af-flex-center af-p-48">
				<Spin />
			</div>
		);
	}

	if (isError || !user) {
		return <Result status="error" title={t("user:message.notFound")} />;
	}

	const hasAvatar = user.avatar?.id && user.avatar?.contentTypeId;
	const avatarUrl = hasAvatar ? `/rest/dms/documents/${user.avatar!.id}/content` : null;

	return (
		<div className="af-preview-container">
			<div className="af-preview-avatar">
				{avatarUrl && !avatarError ? (
					<img
						src={avatarUrl}
						alt={user.name}
						className="af-preview-avatar-image"
						onError={() => setAvatarError(true)}
					/>
				) : (
					<div className="af-preview-avatar-placeholder">{getArea("user")?.icon}</div>
				)}
			</div>

			<div className="af-preview-name">
				<Text className="af-preview-name-text">{user.name}</Text>
			</div>

			<Descriptions column={1} size="small">
				<Descriptions.Item label={t("user:label.email")}>{user.email || "-"}</Descriptions.Item>
				<Descriptions.Item label={t("user:label.role")}>{user.role?.name || "-"}</Descriptions.Item>
				<Descriptions.Item label={t("user:label.tenant")}>
					{user.tenant?.name || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("user:label.owner")}>
					{user.owner?.name || "-"}
				</Descriptions.Item>
			</Descriptions>

			{user.description && (
				<div>
					<Text className="af-preview-description-label">{t("user:label.description")}</Text>
					<Paragraph
						className="af-preview-description-text"
						ellipsis={{ rows: 3, expandable: true }}
					>
						{user.description}
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
