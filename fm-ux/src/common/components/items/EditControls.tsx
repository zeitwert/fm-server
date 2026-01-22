/**
 * EditControls component for the edit/cancel/store button pattern.
 *
 * Displays either:
 * - An "Edit" button when not editing
 * - "Cancel" and "Save" buttons when in edit mode
 */

import { Button, Space } from "antd";
import { EditOutlined, CloseOutlined, SaveOutlined } from "@ant-design/icons";
import { useTranslation } from "react-i18next";

export interface EditControlsProps {
	/** Whether the form is in edit mode */
	isEditing: boolean;
	/** Whether the form has unsaved changes */
	isDirty: boolean;
	/** Whether the form is currently being saved */
	isStoring: boolean;
	/** Whether the user has permission to edit */
	canEdit?: boolean;
	/** Callback when the user clicks the Edit button */
	onEdit: () => void;
	/** Callback when the user clicks the Cancel button */
	onCancel: () => void;
	/** Callback when the user clicks the Save button */
	onStore: () => void;
}

export function EditControls({
	isEditing,
	isDirty,
	isStoring,
	canEdit = true,
	onEdit,
	onCancel,
	onStore,
}: EditControlsProps) {
	const { t } = useTranslation();

	if (!isEditing) {
		return canEdit ? (
			<Space style={{ marginBottom: 8 }}>
				<Button icon={<EditOutlined />} onClick={onEdit} aria-label="common:edit">
					{t("common:action.edit")}
				</Button>
			</Space>
		) : null;
	}

	return (
		<Space style={{ marginBottom: 8 }}>
			<Button icon={<CloseOutlined />} onClick={onCancel} aria-label="common:cancel">
				{t("common:action.cancel")}
			</Button>
			<Button
				type="primary"
				icon={<SaveOutlined />}
				onClick={onStore}
				loading={isStoring}
				disabled={!isDirty}
				aria-label="common:save"
			>
				{t("common:action.save")}
			</Button>
		</Space>
	);
}
