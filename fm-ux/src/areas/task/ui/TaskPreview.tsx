import { Button, Descriptions, Spin, Result, Space, Typography, Tag } from "antd";
import { EditOutlined } from "@ant-design/icons";
import { useNavigate } from "@tanstack/react-router";
import { useTranslation } from "react-i18next";
import { useTask } from "../queries";

const { Text, Paragraph } = Typography;

interface TaskPreviewProps {
	id: string;
	onClose: () => void;
}

export function TaskPreview({ id, onClose }: TaskPreviewProps) {
	const { t } = useTranslation();
	const navigate = useNavigate();

	const { data: task, isLoading, isError } = useTask(id);

	const handleEdit = () => {
		onClose();
		navigate({ to: `/task/${id}` });
	};

	if (isLoading) {
		return (
			<div className="af-flex-center af-p-48">
				<Spin />
			</div>
		);
	}

	if (isError || !task) {
		return <Result status="error" title={t("task:message.notFound")} />;
	}

	return (
		<div className="af-preview-container">
			<div className="af-preview-name">
				<Text className="af-preview-name-text">{task.subject || t("common:label.noTitle")}</Text>
			</div>

			<Descriptions column={1} size="small">
				<Descriptions.Item label={t("task:label.subject")}>{task.subject || "-"}</Descriptions.Item>
				<Descriptions.Item label={t("task:label.stage")}>
					{task.meta?.caseStage?.name || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("task:label.priority")}>
					{task.priority?.name || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("task:label.assignee")}>
					{task.meta?.assignee?.name || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("task:label.relatedTo")}>
					{task.relatedTo?.name || "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("task:label.dueAt")}>
					{task.dueAt
						? new Date(task.dueAt).toLocaleDateString("de-CH", {
								day: "2-digit",
								month: "2-digit",
								year: "numeric",
							})
						: "-"}
				</Descriptions.Item>
				<Descriptions.Item label={t("task:label.isPrivate")}>
					{task.isPrivate ? (
						<Tag color="orange">{t("common:label.yes")}</Tag>
					) : (
						<Tag>{t("common:label.no")}</Tag>
					)}
				</Descriptions.Item>
			</Descriptions>

			{task.content && (
				<div>
					<Text className="af-preview-description-label">{t("task:label.content")}</Text>
					<Paragraph
						className="af-preview-description-text"
						ellipsis={{ rows: 3, expandable: true }}
					>
						{task.content}
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
