import { Col, Row } from "antd";
import { useTranslation } from "react-i18next";
import {
	AfInput,
	AfTextArea,
	AfSelect,
	AfFieldRow,
	AfFieldGroup,
	AfDatePicker,
} from "../../../../common/components/form";
import { useSessionStore } from "../../../../session/model/sessionStore";
import { KERNEL_TENANT } from "../../../../session/model/types";

interface ContactMainFormProps {
	disabled: boolean;
}

export function ContactMainForm({ disabled }: ContactMainFormProps) {
	const { t } = useTranslation();
	const { sessionInfo } = useSessionStore();
	const isKernelTenant = sessionInfo?.tenant?.tenantType?.id === KERNEL_TENANT;

	return (
		<div>
			<Row>
				<Col span={12}>
					<AfFieldGroup legend={t("contact:label.basicInfo")}>
						<AfFieldRow>
							<AfSelect
								name="salutation"
								label={t("contact:label.salutation")}
								source="contact/codeSalutation"
								required
								readOnly={disabled}
								size={6}
							/>
							<AfSelect
								name="title"
								label={t("contact:label.title")}
								source="contact/codeTitle"
								readOnly={disabled}
								size={6}
							/>
							<AfInput
								name="firstName"
								label={t("contact:label.firstName")}
								readOnly={disabled}
								size={12}
							/>
						</AfFieldRow>
						<AfFieldRow>
							<AfInput
								name="lastName"
								label={t("contact:label.lastName")}
								required
								readOnly={disabled}
								size={12}
							/>
							<AfSelect
								name="contactRole"
								label={t("contact:label.contactRole")}
								source="contact/codeContactRole"
								readOnly={disabled}
								size={12}
							/>
						</AfFieldRow>
						<AfFieldRow>
							<AfInput
								name="email"
								label={t("contact:label.email")}
								readOnly={disabled}
								size={12}
							/>
							<AfInput
								name="mobile"
								label={t("contact:label.mobile")}
								readOnly={disabled}
								size={6}
							/>
							<AfInput name="phone" label={t("contact:label.phone")} readOnly={disabled} size={6} />
						</AfFieldRow>
					</AfFieldGroup>
				</Col>
				<Col span={12}>
					<AfFieldGroup legend="&nbsp;">
						<AfTextArea
							name="description"
							label={t("contact:label.description")}
							rows={4}
							readOnly={disabled}
							size={24}
						/>
					</AfFieldGroup>
				</Col>
			</Row>

			<Row>
				<Col span={12}>
					<AfFieldGroup legend={t("contact:label.personal")}>
						<AfFieldRow>
							<AfDatePicker
								name="birthDate"
								label={t("contact:label.birthDate")}
								readOnly={disabled}
								size={8}
							/>
						</AfFieldRow>
					</AfFieldGroup>
				</Col>
				<Col span={12}>
					<AfFieldGroup legend={t("contact:label.organization")}>
						<AfFieldRow>
							{isKernelTenant ? (
								<AfSelect
									name="tenant"
									label={t("contact:label.tenant")}
									source="oe/objTenant"
									required
									readOnly={disabled}
									size={12}
								/>
							) : (
								<AfSelect
									name="tenant"
									label={t("contact:label.tenant")}
									source="oe/objTenant"
									required
									readOnly
									size={12}
								/>
							)}
							<AfSelect
								name="owner"
								label={t("contact:label.owner")}
								source="oe/objUser"
								required
								readOnly={disabled}
								size={12}
							/>
						</AfFieldRow>
					</AfFieldGroup>
				</Col>
			</Row>
		</div>
	);
}
