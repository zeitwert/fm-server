import { Col, Row } from "antd";
import { useTranslation } from "react-i18next";
import {
	AfInput,
	AfTextArea,
	AfSelect,
	AfDatePicker,
	AfCheckbox,
	AfFieldRow,
	AfFieldGroup,
} from "@/common/components/form";

interface TaskMainFormProps {
	disabled: boolean;
}

export function TaskMainForm({ disabled }: TaskMainFormProps) {
	const { t } = useTranslation();

	return (
		<div>
			<Row gutter={16}>
				<Col span={24}>
					<AfFieldGroup legend={t("task:label.basicInfo")}>
						<AfFieldRow>
							<AfInput
								name="subject"
								label={t("task:label.subject")}
								required
								readOnly={disabled}
								size={10}
							/>
							<AfCheckbox
								name="isPrivate"
								label={t("task:label.isPrivate")}
								readOnly={disabled}
								size={2}
							/>
						</AfFieldRow>
						<AfFieldRow>
							<AfTextArea
								name="content"
								label={t("task:label.content")}
								rows={8}
								readOnly={disabled}
								size={24}
							/>
						</AfFieldRow>
						<AfFieldRow>
							<AfSelect
								name="assignee"
								label={t("task:label.assignee")}
								source="oe/objUser"
								readOnly={disabled}
								size={8}
							/>
							<AfDatePicker
								name="dueAt"
								label={t("task:label.dueAt")}
								readOnly={disabled}
								size={8}
							/>
							<AfSelect
								name="priority"
								label={t("task:label.priority")}
								source="task/codeTaskPriority"
								required
								readOnly={disabled}
								size={8}
							/>
						</AfFieldRow>
						<AfFieldRow>
							<AfSelect
								name="relatedTo"
								label={t("task:label.relatedTo")}
								source="oe/objUser"
								required
								readOnly={true}
								size={12}
							/>
						</AfFieldRow>
					</AfFieldGroup>
				</Col>
			</Row>

			<Row gutter={16}>
				<Col span={12}>
					<AfFieldGroup legend={t("task:label.organization")}>
						<AfFieldRow>
							<AfSelect
								name="owner"
								label={t("task:label.owner")}
								source="oe/objUser"
								required
								readOnly={true}
								size={12}
							/>
						</AfFieldRow>
					</AfFieldGroup>
				</Col>
			</Row>
		</div>
	);
}
