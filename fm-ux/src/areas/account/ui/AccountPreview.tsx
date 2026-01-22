import { useEffect, useState } from "react";
import { Button, Descriptions, Spin, Result, Space, Typography } from "antd";
import { BankOutlined, EditOutlined } from "@ant-design/icons";
import { useNavigate } from "@tanstack/react-router";
import { useTranslation } from "react-i18next";
import { useAccount } from "../queries";
import { getLogoUrl } from "../../../common/api/client";

const { Text, Paragraph } = Typography;

interface AccountPreviewProps {
	id: string;
	onClose: () => void;
}

export function AccountPreview({ id, onClose }: AccountPreviewProps) {
	const { t } = useTranslation();
	const navigate = useNavigate();
	const [logoError, setLogoError] = useState(false);

	const { data: account, isLoading, isError } = useAccount(id);

	// Reset logo error when account changes
	useEffect(() => {
		setLogoError(false);
	}, [id]);

	const handleEdit = () => {
		onClose();
		navigate({ to: `/account/${id}` });
	};

	if (isLoading) {
		return (
			<div className="af-flex-center af-p-48">
				<Spin />
			</div>
		);
	}

	if (isError || !account) {
		return <Result status="error" title={t("account:message.notFound")} />;
	}

	const logoUrl = getLogoUrl("account", id);

	return (
		<div className="af-preview-container">
			{/* Logo */}
			<div className="af-preview-avatar">
				{!logoError ? (
					<img
						src={logoUrl}
						alt={account.name}
						className="af-preview-avatar-image"
						onError={() => setLogoError(true)}
					/>
				) : (
					<div className="af-preview-avatar-placeholder">
						<BankOutlined />
					</div>
				)}
			</div>

			{/* Name */}
			<div className="af-preview-name">
				<Text className="af-preview-name-text">{account.name}</Text>
			</div>

			{/* Details */}
			<Descriptions column={1} size="small">
				<Descriptions.Item label={t("account:label.accountType")}>
					{account.accountType?.name || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("account:label.clientSegment")}>
					{account.clientSegment?.name || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("account:label.tenant")}>
					{account.tenant?.name || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("account:label.owner")}>
					{account.owner?.name || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("account:label.mainContact")}>
					{account.mainContact?.name || "-"}
				</Descriptions.Item>
			</Descriptions>

			{/* Description */}
			{account.description && (
				<div>
					<Text className="af-preview-description-label">{t("account:label.description")}</Text>
					<Paragraph
						className="af-preview-description-text"
						ellipsis={{ rows: 3, expandable: true }}
					>
						{account.description}
					</Paragraph>
				</div>
			)}

			{/* Actions */}
			<Space className="af-preview-actions">
				<Button type="primary" icon={<EditOutlined />} onClick={handleEdit}>
					{t("common:action.edit")}
				</Button>
			</Space>
		</div>
	);
}
