import { Col, Row } from "antd";
import { useTranslation } from "react-i18next";
import {
	AfInput,
	AfTextArea,
	AfSelect,
	AfCheckbox,
	AfFieldRow,
	AfFieldGroup,
} from "@/common/components/form";

interface NoteMainFormProps {
	disabled: boolean;
}

export function NoteMainForm({ disabled }: NoteMainFormProps) {
	const { t } = useTranslation();

	return (
		<div>
			<Row gutter={16}>
				<Col span={12}>
					<AfFieldGroup legend={t("note:label.basicInfo")}>
						<AfFieldRow>
							<AfInput
								name="subject"
								label={t("note:label.subject")}
								readOnly={disabled}
								size={24}
							/>
						</AfFieldRow>
						<AfFieldRow>
							<AfSelect
								name="noteType"
								label={t("note:label.noteType")}
								source="collaboration/codeNoteType"
								required
								readOnly={disabled}
								size={12}
							/>
							<AfCheckbox name="isPrivate" label={t("note:label.isPrivate")} disabled={disabled} />
						</AfFieldRow>
					</AfFieldGroup>
				</Col>
				<Col span={12}>
					<AfFieldGroup legend={t("note:label.relatedInfo")}>
						<AfFieldRow>
							<AfInput name="relatedTo.name" label={t("note:label.relatedTo")} readOnly size={24} />
						</AfFieldRow>
					</AfFieldGroup>
				</Col>
			</Row>

			<Row gutter={16}>
				<Col span={24}>
					<AfFieldGroup legend={t("note:label.content")}>
						<AfTextArea
							name="content"
							label={t("note:label.content")}
							rows={8}
							readOnly={disabled}
							size={24}
						/>
					</AfFieldGroup>
				</Col>
			</Row>
		</div>
	);
}
