import { Button, Descriptions, Spin, Result, Space, Typography, theme } from "antd";
import { TeamOutlined, EditOutlined } from "@ant-design/icons";
import { useNavigate } from "@tanstack/react-router";
import { useTranslation } from "react-i18next";
import { useContact } from "../queries";

const { Text, Paragraph } = Typography;
const { useToken } = theme;

interface ContactPreviewProps {
	id: string;
	onClose: () => void;
}

export function ContactPreview({ id, onClose }: ContactPreviewProps) {
	const { t } = useTranslation("contact");
	const { t: tc } = useTranslation("common");
	const navigate = useNavigate();
	const { token } = useToken();

	const { data: contact, isLoading, isError } = useContact(id);

	const handleEdit = () => {
		onClose();
		navigate({ to: `/contact/${id}` });
	};

	if (isLoading) {
		return (
			<div style={{ display: "flex", justifyContent: "center", padding: 40 }}>
				<Spin />
			</div>
		);
	}

	if (isError || !contact) {
		return <Result status="error" title={t("notFound")} />;
	}

	const displayName =
		contact.caption || `${contact.firstName ?? ""} ${contact.lastName ?? ""}`.trim();

	return (
		<div style={{ display: "flex", flexDirection: "column", gap: 16 }}>
			<div style={{ display: "flex", justifyContent: "center", padding: 16 }}>
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
					<TeamOutlined style={{ fontSize: 48, color: token.colorTextQuaternary }} />
				</div>
			</div>

			<div style={{ textAlign: "center" }}>
				<Text strong style={{ fontSize: 18 }}>
					{displayName}
				</Text>
			</div>

			<Descriptions column={1} size="small">
				<Descriptions.Item label={t("account")}>
					{contact.account?.caption || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("email")}>{contact.email || "-"}</Descriptions.Item>
				<Descriptions.Item label={t("mobile")}>{contact.mobile || "-"}</Descriptions.Item>
				<Descriptions.Item label={t("phone")}>{contact.phone || "-"}</Descriptions.Item>
				<Descriptions.Item label={t("owner")}>{contact.owner?.name || "-"}</Descriptions.Item>
			</Descriptions>

			{contact.description && (
				<div>
					<Text type="secondary" style={{ fontSize: 12 }}>
						{t("description")}
					</Text>
					<Paragraph
						style={{ marginTop: 4, marginBottom: 0 }}
						ellipsis={{ rows: 3, expandable: true }}
					>
						{contact.description}
					</Paragraph>
				</div>
			)}

			<Space style={{ marginTop: 8 }}>
				<Button type="primary" icon={<EditOutlined />} onClick={handleEdit}>
					{tc("edit")}
				</Button>
			</Space>
		</div>
	);
}
