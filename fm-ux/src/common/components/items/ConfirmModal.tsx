/**
 * ConfirmModal component for destructive action confirmations.
 *
 * Provides a standardized modal for confirming dangerous operations
 * like deletions, with loading state support.
 */

import { useState } from "react";
import { Modal, Button, Space } from "antd";
import { ExclamationCircleOutlined } from "@ant-design/icons";
import { useTranslation } from "react-i18next";

export interface ConfirmModalProps {
	/** Whether the modal is visible */
	open: boolean;
	/** Modal title */
	title: string;
	/** Description/warning message (supports newlines) */
	description: string;
	/** Whether this is a dangerous/destructive action */
	danger?: boolean;
	/** Custom text for the confirm button */
	confirmText?: string;
	/** Callback when the user confirms (can be async) */
	onConfirm: () => void | Promise<void>;
	/** Callback when the user cancels */
	onCancel: () => void;
}

export function ConfirmModal({
	open,
	title,
	description,
	danger = false,
	confirmText,
	onConfirm,
	onCancel,
}: ConfirmModalProps) {
	const { t } = useTranslation("common");
	const [loading, setLoading] = useState(false);

	const handleConfirm = async () => {
		setLoading(true);
		try {
			await onConfirm();
		} finally {
			setLoading(false);
		}
	};

	return (
		<Modal
			open={open}
			title={
				<Space>
					{danger && <ExclamationCircleOutlined style={{ color: "#ff4d4f" }} />}
					{title}
				</Space>
			}
			onCancel={onCancel}
			footer={[
				<Button key="cancel" onClick={onCancel} aria-label="common:cancel">
					{t("cancel")}
				</Button>,
				<Button
					key="confirm"
					type="primary"
					danger={danger}
					loading={loading}
					onClick={handleConfirm}
					aria-label="common:confirm"
				>
					{confirmText || t("confirm")}
				</Button>,
			]}
			closable={!loading}
			maskClosable={!loading}
		>
			{description.split("\n").map((line, index) => (
				<p key={index} style={{ margin: index === 0 ? 0 : "8px 0 0 0" }}>
					{line}
				</p>
			))}
		</Modal>
	);
}
