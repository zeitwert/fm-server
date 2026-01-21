import { useEffect, useState } from "react";
import { Button, Descriptions, Spin, Result, Space, Typography, theme } from "antd";
import { BankOutlined, EditOutlined } from "@ant-design/icons";
import { useNavigate } from "@tanstack/react-router";
import { useTranslation } from "react-i18next";
import { useAccount } from "../queries";
import { getLogoUrl } from "../../../common/api/client";

const { Text, Paragraph } = Typography;
const { useToken } = theme;

interface AccountPreviewProps {
	id: string;
	onClose: () => void;
}

export function AccountPreview({ id, onClose }: AccountPreviewProps) {
	const { t } = useTranslation("account");
	const { t: tc } = useTranslation("common");
	const navigate = useNavigate();
	const { token } = useToken();
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
			<div style={{ display: "flex", justifyContent: "center", padding: 40 }}>
				<Spin />
			</div>
		);
	}

	if (isError || !account) {
		return <Result status="error" title={t("notFound")} />;
	}

	const logoUrl = getLogoUrl("account", id);

	return (
		<div style={{ display: "flex", flexDirection: "column", gap: 16 }}>
			{/* Logo */}
			<div style={{ display: "flex", justifyContent: "center", padding: 16 }}>
				{!logoError ? (
					<img
						src={logoUrl}
						alt={account.name}
						style={{
							width: 120,
							height: 120,
							borderRadius: 8,
							objectFit: "contain",
							border: `1px solid ${token.colorBorderSecondary}`,
							background: token.colorBgLayout,
						}}
						onError={() => setLogoError(true)}
					/>
				) : (
					<div
						style={{
							width: 120,
							height: 120,
							borderRadius: 8,
							border: `1px solid ${token.colorBorderSecondary}`,
							background: token.colorBgLayout,
							display: "flex",
							alignItems: "center",
							justifyContent: "center",
						}}
					>
						<BankOutlined style={{ fontSize: 48, color: token.colorTextQuaternary }} />
					</div>
				)}
			</div>

			{/* Name */}
			<div style={{ textAlign: "center" }}>
				<Text strong style={{ fontSize: 18 }}>
					{account.name}
				</Text>
			</div>

			{/* Details */}
			<Descriptions column={1} size="small">
				<Descriptions.Item label={t("accountType")}>
					{account.accountType?.name || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("clientSegment")}>
					{account.clientSegment?.name || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("tenant")}>{account.tenant?.name || "-"}</Descriptions.Item>
				<Descriptions.Item label={t("owner")}>{account.owner?.name || "-"}</Descriptions.Item>
				<Descriptions.Item label={t("mainContact")}>
					{account.mainContact?.name || "-"}
				</Descriptions.Item>
			</Descriptions>

			{/* Description */}
			{account.description && (
				<div>
					<Text type="secondary" style={{ fontSize: 12 }}>
						{t("description")}
					</Text>
					<Paragraph
						style={{ marginTop: 4, marginBottom: 0 }}
						ellipsis={{ rows: 3, expandable: true }}
					>
						{account.description}
					</Paragraph>
				</div>
			)}

			{/* Actions */}
			<Space style={{ marginTop: 8 }}>
				<Button type="primary" icon={<EditOutlined />} onClick={handleEdit}>
					{tc("edit")}
				</Button>
			</Space>
		</div>
	);
}
