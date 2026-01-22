/**
 * NotesList component for displaying and managing entity notes.
 *
 * Provides an activity-feed style list with the ability to add new notes.
 */

import { List, Avatar, Typography, Space, Empty, Skeleton, Input, Button, Divider } from "antd";
import { LockOutlined, SendOutlined } from "@ant-design/icons";
import { useState } from "react";
import { useTranslation } from "react-i18next";

const { Text, Paragraph } = Typography;
const { TextArea } = Input;

// ============================================================================
// Types
// ============================================================================

export interface Note {
	id: string;
	subject?: string;
	content: string;
	isPrivate?: boolean;
	meta?: {
		createdAt?: string;
		modifiedAt?: string;
		createdByUser?: {
			id: string;
			name: string;
		};
	};
}

export interface NotesListProps {
	/** Array of notes to display */
	notes: Note[];
	/** Whether the notes are currently loading */
	isLoading?: boolean;
	/** Callback when a new note is submitted */
	onCreateNote?: (content: string) => void;
	/** Whether creation is in progress */
	isCreating?: boolean;
	/** Callback when a note is edited */
	onEditNote?: (note: Note) => void;
	/** Function to generate avatar URL from user ID */
	getAvatarUrl?: (userId: string) => string;
}

// ============================================================================
// Helpers
// ============================================================================

/**
 * Format a date as a relative time string.
 */
function formatRelativeTime(dateString: string | undefined, justNowLabel: string): string {
	if (!dateString) return "";

	const date = new Date(dateString);
	const now = new Date();
	const diffMs = now.getTime() - date.getTime();
	const diffMins = Math.floor(diffMs / 60000);
	const diffHours = Math.floor(diffMs / 3600000);
	const diffDays = Math.floor(diffMs / 86400000);

	if (diffMins < 1) return justNowLabel;
	if (diffMins < 60) return `vor ${diffMins} Min.`;
	if (diffHours < 24) return `vor ${diffHours} Std.`;
	if (diffDays < 7) return `vor ${diffDays} Tagen`;

	return date.toLocaleDateString("de-CH", {
		day: "2-digit",
		month: "2-digit",
		year: "numeric",
	});
}

// ============================================================================
// Component
// ============================================================================

export function NotesList({
	notes,
	isLoading = false,
	onCreateNote,
	isCreating = false,
	onEditNote,
	getAvatarUrl,
}: NotesListProps) {
	const { t } = useTranslation();
	const [newNoteContent, setNewNoteContent] = useState("");

	const handleSubmit = () => {
		if (newNoteContent.trim() && onCreateNote) {
			onCreateNote(newNoteContent.trim());
			setNewNoteContent("");
		}
	};

	const handleKeyDown = (e: React.KeyboardEvent) => {
		// Submit on Ctrl+Enter or Cmd+Enter
		if (e.key === "Enter" && (e.ctrlKey || e.metaKey)) {
			handleSubmit();
		}
	};

	return (
		<div>
			{/* Add new note */}
			{onCreateNote && (
				<>
					<div style={{ display: "flex", gap: 8 }}>
						<TextArea
							value={newNoteContent}
							onChange={(e) => setNewNoteContent(e.target.value)}
							onKeyDown={handleKeyDown}
							placeholder={t("common:action.addNote")}
							autoSize={{ minRows: 2, maxRows: 6 }}
							style={{ flex: 1 }}
						/>
						<Button
							type="primary"
							icon={<SendOutlined />}
							onClick={handleSubmit}
							loading={isCreating}
							disabled={!newNoteContent.trim()}
							aria-label="common:addNote"
						/>
					</div>
					<Divider style={{ margin: "12px 0" }} />
				</>
			)}

			{/* Notes list */}
			{isLoading ? (
				<Skeleton active paragraph={{ rows: 3 }} />
			) : notes.length === 0 ? (
				<Empty description={t("common:message.noNotes")} image={Empty.PRESENTED_IMAGE_SIMPLE} />
			) : (
				<List
					itemLayout="vertical"
					dataSource={notes}
					renderItem={(note) => (
						<List.Item
							actions={
								onEditNote
									? [
										<Button key="edit" type="link" size="small" onClick={() => onEditNote(note)}>
											{t("common:action.edit")}
										</Button>,
									]
									: undefined
							}
							style={{ padding: "12px 0" }}
						>
							<List.Item.Meta
								avatar={
									note.meta?.createdByUser?.id ? (
										<Avatar src={getAvatarUrl?.(note.meta.createdByUser.id)}>
											{note.meta.createdByUser.name?.[0]}
										</Avatar>
									) : (
										<Avatar>?</Avatar>
									)
								}
								title={
									<Space size="small">
										<span>{note.meta?.createdByUser?.name}</span>
										<Text type="secondary" style={{ fontSize: 12 }}>
											{formatRelativeTime(
												note.meta?.modifiedAt || note.meta?.createdAt,
												t("common:label.justNow")
											)}
										</Text>
										{note.isPrivate && (
											<LockOutlined
												style={{ color: "#999", fontSize: 12 }}
												title={t("common:label.private")}
											/>
										)}
									</Space>
								}
							/>
							{note.subject && (
								<Text strong style={{ display: "block", marginBottom: 4 }}>
									{note.subject}
								</Text>
							)}
							<Paragraph
								ellipsis={{ rows: 3, expandable: true, symbol: t("common:label.more") }}
								style={{ marginBottom: 0 }}
							>
								{note.content}
							</Paragraph>
						</List.Item>
					)}
				/>
			)}
		</div>
	);
}
