import { Col, Row } from "antd";
import { useTranslation } from "react-i18next";
import {
	AfInput,
	AfTextArea,
	AfSelect,
	AfFieldRow,
	AfFieldGroup,
} from "../../../../common/components/form";

interface UserMainFormProps {
	disabled: boolean;
}

export function UserMainForm({ disabled }: UserMainFormProps) {
	const { t } = useTranslation();

	return (
		<div>
			<Row>
				<Col span={12}>
					<AfFieldGroup legend={t("user:label.basicInfo")}>
						<AfFieldRow>
							<AfInput
								name="name"
								label={t("user:label.name")}
								required
								readOnly={disabled}
								size={24}
							/>
						</AfFieldRow>
						<AfFieldRow>
							<AfInput
								name="email"
								label={t("user:label.email")}
								required
								readOnly={disabled}
								size={24}
							/>
						</AfFieldRow>
						<AfFieldRow>
							<AfSelect
								name="role"
								label={t("user:label.role")}
								source="oe/codeUserRole"
								required
								readOnly={disabled}
								size={12}
							/>
							<AfSelect
								name="owner"
								label={t("user:label.owner")}
								source="oe/objUser"
								required
								readOnly={disabled}
								size={12}
							/>
						</AfFieldRow>
					</AfFieldGroup>
				</Col>
				<Col span={12}>
					<AfFieldGroup legend="&nbsp;">
						<AfTextArea
							name="description"
							label={t("user:label.description")}
							rows={4}
							readOnly={disabled}
							size={24}
						/>
					</AfFieldGroup>
				</Col>
			</Row>
		</div>
	);
}
