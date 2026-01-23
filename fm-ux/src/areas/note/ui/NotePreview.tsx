import { Button, Descriptions, Spin, Result, Space, Typography } from "antd";
import { EditOutlined, LockOutlined } from "@ant-design/icons";
import { useNavigate } from "@tanstack/react-router";
import { useTranslation } from "react-i18next";
import { useNoteQuery } from "../queries";
import { getArea } from "../../../app/config/AppConfig";

const { Paragraph } = Typography;

interface NotePreviewProps {
	id: string;
	onClose: () => void;
}

export function NotePreview({ id, onClose }: NotePreviewProps) {
	const { t } = useTranslation();
	const navigate = useNavigate();

	const { data: note, isLoading, isError } = useNoteQuery(id);

	const handleEdit = () => {
		onClose();
		navigate({ to: `/note/${id}` });
	};

	if (isLoading) {
		return (
			<div className="af-flex-center af-p-48">
				<Spin />
			</div>
		);
	}

	if (isError || !note) {
		return <Result status="error" title={t("note:message.notFound")} />;
	}

	return (
		<div className="af-preview-container">
			<div className="af-preview-avatar">
				<div className="af-preview-avatar-placeholder">{getArea("note")?.icon}</div>
			</div>

			<div className="af-preview-name">
				<Space>
					<span className="af-preview-name-text">{note.subject || t("common:label.noTitle")}</span>
					{note.isPrivate && (
						<LockOutlined
							style={{ color: "#999", fontSize: 14 }}
							title={t("common:label.private")}
						/>
					)}
				</Space>
			</div>

			<Descriptions column={1} size="small">
				<Descriptions.Item label={t("note:label.noteType")}>
					{note.noteType?.name || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("note:label.relatedTo")}>
					{note.relatedTo?.name || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("note:label.tenant")}>
					{note.tenant?.name || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("note:label.owner")}>
					{note.owner?.name || "-"}
				</Descriptions.Item>
			</Descriptions>

			{note.content && (
				<div>
					<Paragraph
						className="af-preview-description-text"
						ellipsis={{ rows: 3, expandable: true }}
					>
						{note.content}
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
